// RoadExtractionDoc.cpp : CRoadExtractionDoc ���ʵ��
//

#include "stdafx.h"
#include "RoadExtraction.h"

#include "RoadExtractionDoc.h"


#include <fstream>
#include <cmath>


#include <ctime>

#include "RoadExtractionView.h"
#include "MainFrm.h"
#include "RoadPath.h"


#define INF 100000.f

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CRoadExtractionDoc

IMPLEMENT_DYNCREATE(CRoadExtractionDoc, CDocument)

BEGIN_MESSAGE_MAP(CRoadExtractionDoc, CDocument)
	ON_COMMAND(ID_ROAD_EXTRACTION, &CRoadExtractionDoc::OnRoadExtraction)
	ON_COMMAND(ID_POINT_GENERATION, &CRoadExtractionDoc::OnPointGeneration)
	ON_COMMAND(ID_HOTSPOT_ASSIGNMENT, &CRoadExtractionDoc::OnHotspotAssignment)
	ON_COMMAND(ID_SHORTEST_PATH, &CRoadExtractionDoc::OnShortestPath)
	ON_COMMAND(ID_INITIALIZE_SHOW, &CRoadExtractionDoc::OnInitializeShow)
	ON_COMMAND(ID_UPDATE_SHOW, &CRoadExtractionDoc::OnUpdateShow)
END_MESSAGE_MAP()


// CRoadExtractionDoc ����/����

CRoadExtractionDoc::CRoadExtractionDoc()
{
	// TODO: �ڴ����һ���Թ������

	m_pRoadPath = new RoadPath();


	m_colorVehicle[0].val[0] = 0;	m_colorVehicle[0].val[1] = 255; m_colorVehicle[0].val[2] = 50;
	m_colorVehicle[1].val[0] = 255;	m_colorVehicle[1].val[1] = 50; m_colorVehicle[1].val[2] = 0;
	m_colorVehicle[2].val[0] = 0;	m_colorVehicle[2].val[1] = 50;	m_colorVehicle[2].val[2] = 0;

	m_colorNode.val[0] = 100;		m_colorNode.val[1] = 100;		m_colorNode.val[2] = 100;


	srand(time(0));
	m_iHotspotPerVehicle = 5;
}

CRoadExtractionDoc::~CRoadExtractionDoc()
{

	cvReleaseImage(&m_pImageCopy);
	delete m_pRoadPath;
}

BOOL CRoadExtractionDoc::OnNewDocument()
{
	if (!CDocument::OnNewDocument())
		return FALSE;

	// TODO: �ڴ�������³�ʼ������
	// (SDI �ĵ������ø��ĵ�)

	return TRUE;
}




// CRoadExtractionDoc ���л�

void CRoadExtractionDoc::Serialize(CArchive& ar)
{
	if (ar.IsStoring())
	{
		// TODO: �ڴ���Ӵ洢����
	}
	else
	{
		// TODO: �ڴ���Ӽ��ش���
	}
}


// CRoadExtractionDoc ���

#ifdef _DEBUG
void CRoadExtractionDoc::AssertValid() const
{
	CDocument::AssertValid();
}

void CRoadExtractionDoc::Dump(CDumpContext& dc) const
{
	CDocument::Dump(dc);
}
#endif //_DEBUG


// CRoadExtractionDoc ����



// ��ȡImageָ��
CImage& CRoadExtractionDoc::GetImage()
{
	return m_image;
}

// ��ͼƬ
BOOL CRoadExtractionDoc::OnOpenDocument(LPCTSTR lpszPathName)
{
	if (!CDocument::OnOpenDocument(lpszPathName)) 
	{
		return FALSE;
	}
	// TODO: Add your specialized creation code here

	CStringA strAPathName(lpszPathName);
	const char* charPathName = strAPathName;

	m_image.Load(charPathName);

	m_pImageCopy = cvCreateImage(cvGetSize(m_image.GetImage()), m_image.GetImage()->depth, m_image.GetImage()->nChannels);
	cvCopy(m_image.GetImage(), m_pImageCopy, NULL);
	return TRUE;
}

// ����ͼƬ
BOOL CRoadExtractionDoc::OnSaveDocument(LPCTSTR lpszPathName)
{
	// TODO: Add your specialized code here and/or call the base class
	CStringA strAPathName(lpszPathName);
	const char* charPathName = strAPathName;
	m_image.Save(charPathName);
	return TRUE;
	// return CDocument::OnSaveDocument(lpszPathName);
}

// ��·��ȡ
void CRoadExtractionDoc::OnRoadExtraction()
{
	// TODO: �ڴ���������������

	// ���û�м��ص�ͼ�����˳�
	if (!m_image.GetImage())
	{
		return;
	}


	// ���Ƶ�·
	DrawRoad();

	// ����·������
	//float distance = ComputeShortestPath(100, 178, 680, 400);


	// ����View
	UpdateAllViews(NULL);
	CMainFrame * pMainFrame = (CMainFrame*)AfxGetApp()->m_pMainWnd;  
	CRoadExtractionView * pView = (CRoadExtractionView*)pMainFrame->GetActiveView();  
	pView->UpdateWindow();


}


void CRoadExtractionDoc::DrawRoad()
{
	//�����·�ڵ���Ϣ
	std::fstream inputFileStream;
	inputFileStream.open("..\\bin\\road_notes.txt", std::ifstream::in);
	int index;
	int x, y;
	std::vector<int> vecX;
	std::vector<int> vecY;
	while(inputFileStream >> index >> x >> y)
	{
		vecX.push_back(x);
		vecY.push_back(y);
	}
	inputFileStream.close();
	inputFileStream.clear();

	// ����ڵ��ڽӹ�ϵ�����Ƶ�·
	int numAdjacentNodes;
	inputFileStream.open("..\\bin\\notes_adjacency.txt");
	for (int i = 0; i < vecX.size(); ++i)
	{
		inputFileStream >> index >> numAdjacentNodes;
		for (int j = 0; j < numAdjacentNodes; ++j)
		{
			// �ڽӵ�����
			inputFileStream	>> index;
			cvLine(m_image.GetImage(), cvPoint(vecX[i], vecY[i]), cvPoint(vecX[index], vecY[index]), CV_RGB(255,0,0), 1);
		}
	}
	inputFileStream.close();
	inputFileStream.clear();
}

// ����·��������¼�����Ľڵ���Ϣ
float CRoadExtractionDoc::ComputeShortestPath(int srcIndex, int dstIndex, std::vector<int>& path,
						  CvScalar color, bool isDrawPath, int thickness)
{
	//�����·�ڵ���Ϣ
	std::fstream inputFileStream;
	inputFileStream.open("..\\bin\\road_notes.txt", std::ifstream::in);
	int index;
	int x, y;
	std::vector<int> vecX;
	std::vector<int> vecY;
	while(inputFileStream >> index >> x >> y)
	{
		vecX.push_back(x);
		vecY.push_back(y);
	}
	inputFileStream.close();
	inputFileStream.clear();


	float distance = 0;


	// ����ڵ������·��
	// �ҵ������·���ڵ���Ϣ������vector��
	distance += m_pRoadPath->ComputeShortestPath(srcIndex,dstIndex, path);

	//if (isDrawPath)
	//{
	//	if (shortestPath.size() > 0)
	//	{
	//		for (int i = 0; i < shortestPath.size() - 1; ++i)
	//		{
	//			cvLine(m_image.GetImage(), cvPoint(vecX[shortestPath[i]], vecY[shortestPath[i]]), 
	//				cvPoint(vecX[shortestPath[i + 1]], vecY[shortestPath[i + 1]]), color, 2);
	//		}
	//	}
	//}

	return distance;
}
float CRoadExtractionDoc::ComputeShortestPath(int srcIndex, int dstIndex, 
						  CvScalar color, bool isDrawPath, int thickness)
{
	//�����·�ڵ���Ϣ
	std::fstream inputFileStream;
	inputFileStream.open("..\\bin\\road_notes.txt", std::ifstream::in);
	int index;
	int x, y;
	std::vector<int> vecX;
	std::vector<int> vecY;
	while(inputFileStream >> index >> x >> y)
	{
		vecX.push_back(x);
		vecY.push_back(y);
	}
	inputFileStream.close();
	inputFileStream.clear();


	float distance = 0;


	// ����ڵ������·��
	// �ҵ������·���ڵ���Ϣ���������vector��
	std::vector<int> shortestPath;
	//float distance;
	distance += m_pRoadPath->ComputeShortestPath(srcIndex,dstIndex, shortestPath);

	if (isDrawPath)
	{
		if (shortestPath.size() > 0)
		{
			for (int i = 0; i < shortestPath.size() - 1; ++i)
			{
				cvLine(m_image.GetImage(), cvPoint(vecX[shortestPath[i]], vecY[shortestPath[i]]), 
					cvPoint(vecX[shortestPath[i + 1]], vecY[shortestPath[i + 1]]), color, thickness);
			}
		}
	}

	return distance;
}



float CRoadExtractionDoc::ComputeShortestPath(int srcX, int srcY, int dstX, int dstY, 
											  CvScalar color, bool isDrawPath, int thickness)
{
	//�����·�ڵ���Ϣ
	std::fstream inputFileStream;
	inputFileStream.open("..\\bin\\road_notes.txt", std::ifstream::in);
	int index;
	int x, y;
	std::vector<int> vecX;
	std::vector<int> vecY;
	while(inputFileStream >> index >> x >> y)
	{
		vecX.push_back(x);
		vecY.push_back(y);
	}
	inputFileStream.close();
	inputFileStream.clear();

	// �ҵ�������յ��Ӧ�ĵ�·�ڵ�
	int srcNode;
	int dstNode;
	float srcDistance = INF;
	float dstDistance = INF;
	float tempDistance;
	float distance;
	for (int i = 0; i < vecX.size(); ++i)
	{
		tempDistance = sqrt(pow(1.f * vecX[i] - srcX, 2) + pow(1.f * vecY[i] - srcY, 2));
		if (tempDistance < srcDistance)
		{
			srcDistance = tempDistance;
			srcNode = i;
		}

		tempDistance = sqrt(pow(1.f * vecX[i] - dstX, 2) + pow(1.f * vecY[i] - dstY, 2));
		if (tempDistance < dstDistance)
		{
			dstDistance = tempDistance;
			dstNode = i;
		}
	}
	distance = srcDistance + dstDistance;


	// ����ڵ������·��
	// �ҵ������·���ڵ���Ϣ���������vector��
	std::vector<int> shortestPath;
	//float distance;
	distance += m_pRoadPath->ComputeShortestPath(srcNode,dstNode, shortestPath);

	if (isDrawPath)
	{

		if (shortestPath.size() > 0)
		{			
			for (int i = 0; i < shortestPath.size() - 1; ++i)
			{
				cvLine(m_image.GetImage(), cvPoint(vecX[shortestPath[i]], vecY[shortestPath[i]]), 
					cvPoint(vecX[shortestPath[i + 1]], vecY[shortestPath[i + 1]]), color, thickness);
			}
			cvLine(m_image.GetImage(), cvPoint(vecX[shortestPath[0]], vecY[shortestPath[0]]), 
				cvPoint(dstX, dstY), color, thickness);
			cvLine(m_image.GetImage(), cvPoint(vecX[shortestPath[shortestPath.size() -1]], vecY[shortestPath[shortestPath.size() -1]]), 
				cvPoint(srcX, srcY), color, thickness);
		}
	}

	return distance;
}


// Ϊ��ʾ���ɵ�
void CRoadExtractionDoc::GeneratePointsForShow()
{
	//�����·�ڵ���Ϣ
	std::fstream inputFileStream;
	inputFileStream.open("..\\bin\\road_notes.txt", std::ifstream::in);
	int index;
	int x, y;
	std::vector<int> vecX;
	std::vector<int> vecY;
	while(inputFileStream >> index >> x >> y)
	{
		vecX.push_back(x);
		vecY.push_back(y);
	}
	inputFileStream.close();
	inputFileStream.clear();

	for (int i = 0; i < vecX.size(); ++i)
	{
		cvCircle(m_image.GetImage(), cvPoint(vecX[i], vecY[i]), 2, m_colorNode, 2);
	}

	// ���ɳ���
	int randNum = rand() % vecX.size();
	m_vehicleForShow.no = randNum;
	m_vehicleForShow.point = cvPoint(vecX[randNum], vecY[randNum]);
	cvCircle(m_image.GetImage(), m_vehicleForShow.point, 10, CV_RGB(0, 255, 0), -1);

	PointInfo tempPointInfo;

	// �����ȵ�
	for (int i = 0; i < NUM_HOTSPOT_FOR_SHOW; ++i)
	{
		int randNum = rand() % vecX.size();
		tempPointInfo.no = randNum;
		tempPointInfo.point.x = vecX[randNum];
		tempPointInfo.point.y = vecY[randNum];

		// ���Ʋ������ظ��ȵ�
		bool isExist = false;
		for (int j = 0; j < m_vecHotspotForShow.size(); ++j)
		{
			if (randNum == m_vecHotspotForShow[j].no || randNum == m_vehicleForShow.no)
			{
				isExist = true;
				break;
			}
		}


		// ����Ѵ��ڸ��ȵ㣬����������һ��
		if (isExist)
		{
			--i;
			continue;
		}

		m_vecHotspotForShow.push_back(tempPointInfo);

	}

	// �����ȵ�
	for (int i = 0; i < NUM_HOTSPOT_FOR_SHOW; ++i)
	{
		m_iFlagForShow.push_back(0);
		cvCircle(m_image.GetImage(), m_vecHotspotForShow[i].point, 5, CV_RGB(255,0,0), 2);
	}

}



// ��������ȵ㼰������
void CRoadExtractionDoc::GeneratePoint()
{

	//�����·�ڵ���Ϣ
	std::fstream inputFileStream;
	inputFileStream.open("..\\bin\\road_notes.txt", std::ifstream::in);
	int index;
	int x, y;
	std::vector<int> vecX;
	std::vector<int> vecY;
	while(inputFileStream >> index >> x >> y)
	{
		vecX.push_back(x);
		vecY.push_back(y);
	}
	inputFileStream.close();
	inputFileStream.clear();



	PointInfo tempPointInfo;



	// ���ɳ���
	for (int i = 0; i < NUM_VEHICLE; ++i)
	{
		int randNum  = rand() % vecX.size();
		tempPointInfo.no = randNum;
		tempPointInfo.point.x = vecX[randNum];
		tempPointInfo.point.y = vecY[randNum];


		m_vecVehicle.push_back(tempPointInfo);

	}

	// ���Ƴ���
	for (int i = 0; i < NUM_VEHICLE; ++i)
	{
		cvCircle(m_image.GetImage(), m_vecVehicle[i].point, 10, m_colorVehicle[i], -1);
	}





	// �����ȵ�
	for (int i = 0; i < NUM_HOTSPOT; ++i)
	{
		int randNum = rand() % vecX.size();
		tempPointInfo.no = randNum;
		tempPointInfo.point.x = vecX[randNum];
		tempPointInfo.point.y = vecY[randNum];

		// ���Ʋ������ظ��ȵ�
		bool isExist = false;
		for (int j = 0; j < m_vecHotspot.size(); ++j)
		{
			if (randNum == m_vecHotspot[j].no)
			{
				isExist = true;
				break;
			}
		}

		for (int j = 0; j < m_vecVehicle.size(); ++j)
		{
			if (randNum == m_vecVehicle[j].no)
			{
				isExist = true;
				break;
			}
		}

		// ����Ѵ��ڸ��ȵ㣬����������һ��
		if (isExist)
		{
			--i;
			continue;
		}

		m_vecHotspot.push_back(tempPointInfo);

	}

	// �����ȵ�
	for (int i = 0; i < NUM_HOTSPOT; ++i)
	{
		cvCircle(m_image.GetImage(), m_vecHotspot[i].point, 5, CV_RGB(255,0,0), 2);
	}

}

void CRoadExtractionDoc::OnPointGeneration()
{
	// TODO: �ڴ���������������


	// ���û�м��ص�ͼ�����˳�
	if (!m_image.GetImage())
	{
		return;
	}

	GeneratePoint();

	// ����View
	UpdateAllViews(NULL);
	CMainFrame * pMainFrame = (CMainFrame*)AfxGetApp()->m_pMainWnd;  
	CRoadExtractionView * pView = (CRoadExtractionView*)pMainFrame->GetActiveView();  
	pView->UpdateWindow();


}

// �����ȵ�
void CRoadExtractionDoc::AssignHotspot()
{


	// �����ȵ㣬��֤ͬһ��������������������ͬ���ȵ�
	bool isExist = false;


	std::vector<PointInfo> tempPointInfo;
	// ����ÿһ����
	for (int i = 0; i < NUM_VEHICLE; ++i)
	{
		// ���ȷ���n���ȵ������
		int n = rand() % (NUM_HOTSPOT - m_iHotspotPerVehicle) + m_iHotspotPerVehicle;
		for (int j = 0; j < n; ++j)
		{
			int randNum = rand() % NUM_HOTSPOT;
			isExist = false;
			for (int k = 0; k < tempPointInfo.size(); ++k)
			{
				if (m_vecHotspot[randNum].no == tempPointInfo[k].no || m_vecHotspot[randNum].no == m_vecVehicle[i].no)
				{
					isExist = true;
					break;
				}

			}

			if (isExist)
			{
				--j;
				continue;
			}
			tempPointInfo.push_back(m_vecHotspot[randNum]);
			//cvCircle(m_image.GetImage(), m_hotspotPerVehicle[i][j].point, 5, m_colorVehicle[i], 2);
		}

		// �Ҿ�����̵�m���ȵ�
		// ���λ		
		std::vector<int> flag;
		for (int j = 0; j < n; ++j)
		{
			flag.push_back(0);
		}
		for (int j = 0; j < m_iHotspotPerVehicle; ++j)
		{
			float tempDistance = INF;
			int tempIndex = -1;
			for (int k = 0; k < n; ++k)
			{
				if (0 == flag[k])
				{
					float temp = ComputeShortestPath(m_vecVehicle[i].no, tempPointInfo[k].no, cvScalar(0, 0, 0, 0), false);
					if (temp < tempDistance)
					{
						tempDistance = temp;
						tempIndex = k;
					}
				}
			}
			m_hotspotPerVehicle[i].push_back(tempPointInfo[tempIndex]);
			cvCircle(m_image.GetImage(), m_hotspotPerVehicle[i][j].point, 5, m_colorVehicle[i], 2);
			flag[tempIndex] = 1;
		}
		flag.clear();
		tempPointInfo.clear();

	}

	std::ofstream ouputFileStream;
	ouputFileStream.open("debug.txt");
	
	for (int i = 0; i < NUM_VEHICLE; ++i)
	{
		ouputFileStream << m_vecVehicle[i].no << "\t";
		for (int j = 0; j < m_iHotspotPerVehicle; ++j)
		{
			ouputFileStream << m_hotspotPerVehicle[i][j].no << "\t";	
		}
		ouputFileStream << std::endl;
	}
	ouputFileStream.close();
	ouputFileStream.clear();


}

void CRoadExtractionDoc::OnHotspotAssignment()
{
	// TODO: �ڴ���������������

	// ���û�м��ص�ͼ�����˳�
	if (!m_image.GetImage())
	{
		return;
	}


	AssignHotspot();
	

	// ����View
	UpdateAllViews(NULL);
	CMainFrame * pMainFrame = (CMainFrame*)AfxGetApp()->m_pMainWnd;  
	CRoadExtractionView * pView = (CRoadExtractionView*)pMainFrame->GetActiveView();  
	pView->UpdateWindow();

}

// ���㳵���˶������·��
void CRoadExtractionDoc::ComputeVehiclePath()
{
	// ���ڴ洢ÿ��������̾���
	std::vector<float> vecDistance;

	// ��ÿһ��������·��
	for (int i = 0; i < NUM_VEHICLE; ++i)
	{
		// �����ȵ���������·������̵��ȵ㣬Ȼ���ڵ�ǰ�ȵ㣬
		// �ټ��������·������̵��ȵ㣬���μ��㲢�˶�

		// ����Ƿ����߹����ȵ�
		std::vector<int> flag;
		for (int j = 0; j < m_hotspotPerVehicle[i].size(); ++j)
		{
			flag.push_back(0);
		}
		int nextNode = -1;
		int tempSrcNode = m_vecVehicle[i].no;
		float distance = 0.f;
		// 
		for (int j = 0; j < m_hotspotPerVehicle[i].size(); ++j)
		{
			float tempDistance = INF;
			for (int k = 0; k < m_hotspotPerVehicle[i].size(); ++k)
			{

				if (0 == flag[k])
				{
					float temp = ComputeShortestPath(
						tempSrcNode, m_hotspotPerVehicle[i][k].no, cvScalar(0, 0, 0, 0), false);
					if (temp < tempDistance)
					{
						tempDistance = temp;
						nextNode = k;
					}

				}
			}

			distance += ComputeShortestPath(
				tempSrcNode, m_hotspotPerVehicle[i][nextNode].no, m_colorVehicle[i]);
			tempSrcNode = m_hotspotPerVehicle[i][nextNode].no;
			flag[nextNode] = 1;
		}

		vecDistance.push_back(distance);
	}
}


void CRoadExtractionDoc::OnShortestPath()
{
	// TODO: �ڴ���������������

	// ���û�м��ص�ͼ�����˳�
	if (!m_image.GetImage())
	{
		return;
	}

	ComputeVehiclePath();



	// ����View
	UpdateAllViews(NULL);
	CMainFrame * pMainFrame = (CMainFrame*)AfxGetApp()->m_pMainWnd;  
	CRoadExtractionView * pView = (CRoadExtractionView*)pMainFrame->GetActiveView();  
	pView->UpdateWindow();

}


void CRoadExtractionDoc::SetHotspotPerVehicle(int m)
{
	m_iHotspotPerVehicle = m;
}
void CRoadExtractionDoc::OnInitializeShow()
{
	// TODO: �ڴ���������������
	// ���û�м��ص�ͼ�����˳�
	if (!m_image.GetImage())
	{
		return;
	}


	InitShow();

	// ����View
	UpdateAllViews(NULL);
	CMainFrame * pMainFrame = (CMainFrame*)AfxGetApp()->m_pMainWnd;  
	CRoadExtractionView * pView = (CRoadExtractionView*)pMainFrame->GetActiveView();  
	pView->UpdateWindow();

}

void CRoadExtractionDoc::OnUpdateShow()
{
	// TODO: �ڴ���������������
	if (!m_image.GetImage())
	{
		return;
	}

	UpdateShow();

	// ����View
	UpdateAllViews(NULL);
	CMainFrame * pMainFrame = (CMainFrame*)AfxGetApp()->m_pMainWnd;  
	CRoadExtractionView * pView = (CRoadExtractionView*)pMainFrame->GetActiveView();  
	pView->UpdateWindow();
}

// ��ʼ����ʾ����
void CRoadExtractionDoc::InitShow()
{
	GeneratePointsForShow();

	//std::vector<int> path;
	//float distance = ComputeShortestPath(3,16, 
	//	path, cvScalar(0, 0, 0, 0), false);
	m_iCurrentM = m_iHotspotPerVehicle;


}


// ������ʾ
void CRoadExtractionDoc::UpdateShow()
{

	// ���յ�ͼ�񿽻�,�൱���ػ�
	cvCopy(m_pImageCopy, m_image.GetImage(), NULL);

	// ��·�������
	std::fstream inputFileStream;
	inputFileStream.open("..\\bin\\road_notes.txt", std::ifstream::in);
	int index;
	int x, y;
	std::vector<int> vecX;
	std::vector<int> vecY;
	while(inputFileStream >> index >> x >> y)
	{
		vecX.push_back(x);
		vecY.push_back(y);
	}
	inputFileStream.close();
	inputFileStream.clear();

	for (int i = 0; i < vecX.size(); ++i)
	{
		cvCircle(m_image.GetImage(), cvPoint(vecX[i], vecY[i]), 2, m_colorNode, 2);
	}




	// ���m����0�ˣ����˳�
	if (0 ==  m_iCurrentM)
	{


		m_vecAssignedHotspotForShow.clear();
		// ���������ȵ�
		for (int i = 0; i < m_vecHotspotForShow.size(); ++i)
		{
			cvCircle(m_image.GetImage(), m_vecHotspotForShow[i].point, 5, CV_RGB(255,0,0), 2);
		}

		// ����ѡ�е�m���ȵ�
		for (int i = 0; i < m_vecAssignedHotspotForShow.size(); ++i)
		{
			cvCircle(m_image.GetImage(), m_vecAssignedHotspotForShow[i].point, 5, CV_RGB(0,255,0), 2);
		}

		// �����߹���·��
		if (m_vecPathDonedForShow.size() > 0)
		{
			for (int i = 0; i < m_vecPathDonedForShow.size() - 1; ++i)
			{
				cvLine(m_image.GetImage(), m_vecPathDonedForShow[i], m_vecPathDonedForShow[i + 1], CV_RGB(0, 50, 50), 2);
			}
		}


		// ���Ƴ���
		cvCircle(m_image.GetImage(), m_vehicleForShow.point, 8, CV_RGB(0, 255, 0), -1);
		return;
	}


	
	// ����m������ȵ�
	// ���λ
	std::vector<int> flag;
	for (int j = 0; j < m_vecHotspotForShow.size(); ++j)
	{
		flag.push_back(0);
	}
	for (int j = 0; j < m_iCurrentM; ++j)
	{
		float tempDistance = INF;
		int tempIndex = -1;
		for (int k = 0; k < m_vecHotspotForShow.size(); ++k)
		{
			if (0 == flag[k] && 0 == m_iFlagForShow[k])
			{
				float temp = ComputeShortestPath(m_vehicleForShow.no, m_vecHotspotForShow[k].no, cvScalar(0, 0, 0, 0), false);
				if (temp < tempDistance)
				{
					tempDistance = temp;
					tempIndex = k;
				}
			}
		}
		m_vecAssignedHotspotForShow.push_back(m_vecHotspotForShow[tempIndex]);
		
		//cvCircle(m_image.GetImage(), m_vecAssignedHotspotForShow[j].point, 5, CV_RGB(0, 255, 0), 2);
		flag[tempIndex] = 1;
	}


	// �������
	// ����Ƿ�������߹����ȵ�
	flag.clear();
	for (int j = 0; j < m_vecAssignedHotspotForShow.size(); ++j)
	{
		flag.push_back(0);
	}
	int nextNodeTobeDelete = -1; // ��¼��һ��Ҫ������ȵ㣬��Ҫ��ɾ��
	int nextNode = -1;
	int tempSrcNode = m_vehicleForShow.no;
	float distance = 0.f;
	// 
	for (int j = 0; j < m_vecAssignedHotspotForShow.size(); ++j)
	{
		float tempDistance = INF;
		for (int k = 0; k < m_vecAssignedHotspotForShow.size(); ++k)
		{

			if (0 == flag[k])
			{
				float temp = ComputeShortestPath(
					tempSrcNode, m_vecAssignedHotspotForShow[k].no, cvScalar(0, 0, 0, 0), false);
				if (temp < tempDistance)
				{
					tempDistance = temp;
					nextNode = k;
				}

			}
		}
		if (0 == j)
		{
			nextNodeTobeDelete = nextNode;
		}

		distance += ComputeShortestPath(
			tempSrcNode, m_vecAssignedHotspotForShow[nextNode].no, CV_RGB(0, 255, 0), true, 1);
		tempSrcNode = m_vecAssignedHotspotForShow[nextNode].no;
		flag[nextNode] = 1;
	}



	// ����

	// ���������ȵ�
	for (int i = 0; i < m_vecHotspotForShow.size(); ++i)
	{
		cvCircle(m_image.GetImage(), m_vecHotspotForShow[i].point, 5, CV_RGB(255,0,0), 2);
	}

	// ����ѡ�е�m���ȵ�
	for (int i = 0; i < m_vecAssignedHotspotForShow.size(); ++i)
	{
		cvCircle(m_image.GetImage(), m_vecAssignedHotspotForShow[i].point, 5, CV_RGB(0,255,0), 2);
	}

	// �����߹���·��
	if (m_vecPathDonedForShow.size() > 0)
	{
		for (int i = 0; i < m_vecPathDonedForShow.size() - 1; ++i)
		{
			cvLine(m_image.GetImage(), m_vecPathDonedForShow[i], m_vecPathDonedForShow[i + 1], CV_RGB(0, 50, 50), 2);
		}
	}


	// ���Ƴ���
	cvCircle(m_image.GetImage(), m_vehicleForShow.point, 8, CV_RGB(0, 255, 0), -1);




	// ������һ�̻ᵽ����ȵ㣬�����߹���·��
	std::vector<int> path;
	distance = ComputeShortestPath(m_vehicleForShow.no, m_vecAssignedHotspotForShow[nextNodeTobeDelete].no, 
		path, cvScalar(0, 0, 0, 0), false);

	for (int i = 0; i < path.size(); ++i)
	{
		m_vecPathDonedForShow.push_back(cvPoint(vecX[path[path.size() - i - 1]], vecY[path[path.size() - i - 1]]));
	}

	// �����������;�е��ȵ㣬�����ȵ��־Ϊ1
	for (int i = 0; i < m_vecHotspotForShow.size(); ++i)
	{
		if (m_vecHotspotForShow[i].no == m_vehicleForShow.no)
		{
			m_iFlagForShow[i] = 1;
		}
	}

	m_vehicleForShow = m_vecAssignedHotspotForShow[nextNodeTobeDelete];
	m_vecAssignedHotspotForShow.clear();





	--m_iCurrentM;

}
