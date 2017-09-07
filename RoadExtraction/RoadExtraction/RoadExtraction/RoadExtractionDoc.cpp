// RoadExtractionDoc.cpp : CRoadExtractionDoc 类的实现
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


// CRoadExtractionDoc 构造/析构

CRoadExtractionDoc::CRoadExtractionDoc()
{
	// TODO: 在此添加一次性构造代码

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

	// TODO: 在此添加重新初始化代码
	// (SDI 文档将重用该文档)

	return TRUE;
}




// CRoadExtractionDoc 序列化

void CRoadExtractionDoc::Serialize(CArchive& ar)
{
	if (ar.IsStoring())
	{
		// TODO: 在此添加存储代码
	}
	else
	{
		// TODO: 在此添加加载代码
	}
}


// CRoadExtractionDoc 诊断

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


// CRoadExtractionDoc 命令



// 获取Image指针
CImage& CRoadExtractionDoc::GetImage()
{
	return m_image;
}

// 打开图片
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

// 保存图片
BOOL CRoadExtractionDoc::OnSaveDocument(LPCTSTR lpszPathName)
{
	// TODO: Add your specialized code here and/or call the base class
	CStringA strAPathName(lpszPathName);
	const char* charPathName = strAPathName;
	m_image.Save(charPathName);
	return TRUE;
	// return CDocument::OnSaveDocument(lpszPathName);
}

// 道路提取
void CRoadExtractionDoc::OnRoadExtraction()
{
	// TODO: 在此添加命令处理程序代码

	// 如果没有加载地图，则退出
	if (!m_image.GetImage())
	{
		return;
	}


	// 绘制道路
	DrawRoad();

	// 计算路径距离
	//float distance = ComputeShortestPath(100, 178, 680, 400);


	// 更新View
	UpdateAllViews(NULL);
	CMainFrame * pMainFrame = (CMainFrame*)AfxGetApp()->m_pMainWnd;  
	CRoadExtractionView * pView = (CRoadExtractionView*)pMainFrame->GetActiveView();  
	pView->UpdateWindow();


}


void CRoadExtractionDoc::DrawRoad()
{
	//读入道路节点信息
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

	// 读入节点邻接关系，绘制道路
	int numAdjacentNodes;
	inputFileStream.open("..\\bin\\notes_adjacency.txt");
	for (int i = 0; i < vecX.size(); ++i)
	{
		inputFileStream >> index >> numAdjacentNodes;
		for (int j = 0; j < numAdjacentNodes; ++j)
		{
			// 邻接点的序号
			inputFileStream	>> index;
			cvLine(m_image.GetImage(), cvPoint(vecX[i], vecY[i]), cvPoint(vecX[index], vecY[index]), CV_RGB(255,0,0), 1);
		}
	}
	inputFileStream.close();
	inputFileStream.clear();
}

// 计算路径，并记录经过的节点信息
float CRoadExtractionDoc::ComputeShortestPath(int srcIndex, int dstIndex, std::vector<int>& path,
						  CvScalar color, bool isDrawPath, int thickness)
{
	//读入道路节点信息
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


	// 计算节点间的最短路径
	// 找到的最短路径节点信息保存在vector中
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
	//读入道路节点信息
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


	// 计算节点间的最短路径
	// 找到的最短路径节点信息保存在这个vector中
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
	//读入道路节点信息
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

	// 找到起点与终点对应的道路节点
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


	// 计算节点间的最短路径
	// 找到的最短路径节点信息保存在这个vector中
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


// 为演示生成点
void CRoadExtractionDoc::GeneratePointsForShow()
{
	//读入道路节点信息
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

	// 生成车辆
	int randNum = rand() % vecX.size();
	m_vehicleForShow.no = randNum;
	m_vehicleForShow.point = cvPoint(vecX[randNum], vecY[randNum]);
	cvCircle(m_image.GetImage(), m_vehicleForShow.point, 10, CV_RGB(0, 255, 0), -1);

	PointInfo tempPointInfo;

	// 生成热点
	for (int i = 0; i < NUM_HOTSPOT_FOR_SHOW; ++i)
	{
		int randNum = rand() % vecX.size();
		tempPointInfo.no = randNum;
		tempPointInfo.point.x = vecX[randNum];
		tempPointInfo.point.y = vecY[randNum];

		// 控制不生成重复热点
		bool isExist = false;
		for (int j = 0; j < m_vecHotspotForShow.size(); ++j)
		{
			if (randNum == m_vecHotspotForShow[j].no || randNum == m_vehicleForShow.no)
			{
				isExist = true;
				break;
			}
		}


		// 如果已存在该热点，则重新生成一次
		if (isExist)
		{
			--i;
			continue;
		}

		m_vecHotspotForShow.push_back(tempPointInfo);

	}

	// 绘制热点
	for (int i = 0; i < NUM_HOTSPOT_FOR_SHOW; ++i)
	{
		m_iFlagForShow.push_back(0);
		cvCircle(m_image.GetImage(), m_vecHotspotForShow[i].point, 5, CV_RGB(255,0,0), 2);
	}

}



// 生成随机热点及车辆点
void CRoadExtractionDoc::GeneratePoint()
{

	//读入道路节点信息
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



	// 生成车辆
	for (int i = 0; i < NUM_VEHICLE; ++i)
	{
		int randNum  = rand() % vecX.size();
		tempPointInfo.no = randNum;
		tempPointInfo.point.x = vecX[randNum];
		tempPointInfo.point.y = vecY[randNum];


		m_vecVehicle.push_back(tempPointInfo);

	}

	// 绘制车辆
	for (int i = 0; i < NUM_VEHICLE; ++i)
	{
		cvCircle(m_image.GetImage(), m_vecVehicle[i].point, 10, m_colorVehicle[i], -1);
	}





	// 生成热点
	for (int i = 0; i < NUM_HOTSPOT; ++i)
	{
		int randNum = rand() % vecX.size();
		tempPointInfo.no = randNum;
		tempPointInfo.point.x = vecX[randNum];
		tempPointInfo.point.y = vecY[randNum];

		// 控制不生成重复热点
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

		// 如果已存在该热点，则重新生成一次
		if (isExist)
		{
			--i;
			continue;
		}

		m_vecHotspot.push_back(tempPointInfo);

	}

	// 绘制热点
	for (int i = 0; i < NUM_HOTSPOT; ++i)
	{
		cvCircle(m_image.GetImage(), m_vecHotspot[i].point, 5, CV_RGB(255,0,0), 2);
	}

}

void CRoadExtractionDoc::OnPointGeneration()
{
	// TODO: 在此添加命令处理程序代码


	// 如果没有加载地图，则退出
	if (!m_image.GetImage())
	{
		return;
	}

	GeneratePoint();

	// 更新View
	UpdateAllViews(NULL);
	CMainFrame * pMainFrame = (CMainFrame*)AfxGetApp()->m_pMainWnd;  
	CRoadExtractionView * pView = (CRoadExtractionView*)pMainFrame->GetActiveView();  
	pView->UpdateWindow();


}

// 分配热点
void CRoadExtractionDoc::AssignHotspot()
{


	// 分配热点，保证同一个车辆，不分配两个相同的热点
	bool isExist = false;


	std::vector<PointInfo> tempPointInfo;
	// 对于每一辆车
	for (int i = 0; i < NUM_VEHICLE; ++i)
	{
		// 首先分配n个热点给车辆
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

		// 找距离最短的m个热点
		// 标记位		
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
	// TODO: 在此添加命令处理程序代码

	// 如果没有加载地图，则退出
	if (!m_image.GetImage())
	{
		return;
	}


	AssignHotspot();
	

	// 更新View
	UpdateAllViews(NULL);
	CMainFrame * pMainFrame = (CMainFrame*)AfxGetApp()->m_pMainWnd;  
	CRoadExtractionView * pView = (CRoadExtractionView*)pMainFrame->GetActiveView();  
	pView->UpdateWindow();

}

// 计算车辆运动的最短路径
void CRoadExtractionDoc::ComputeVehiclePath()
{
	// 用于存储每辆车的最短距离
	std::vector<float> vecDistance;

	// 对每一辆车计算路径
	for (int i = 0; i < NUM_VEHICLE; ++i)
	{
		// 车辆先到达离它道路距离最短的热点，然后在当前热点，
		// 再计算与其道路距离最短的热点，依次计算并运动

		// 标记是否已走过该热点
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
	// TODO: 在此添加命令处理程序代码

	// 如果没有加载地图，则退出
	if (!m_image.GetImage())
	{
		return;
	}

	ComputeVehiclePath();



	// 更新View
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
	// TODO: 在此添加命令处理程序代码
	// 如果没有加载地图，则退出
	if (!m_image.GetImage())
	{
		return;
	}


	InitShow();

	// 更新View
	UpdateAllViews(NULL);
	CMainFrame * pMainFrame = (CMainFrame*)AfxGetApp()->m_pMainWnd;  
	CRoadExtractionView * pView = (CRoadExtractionView*)pMainFrame->GetActiveView();  
	pView->UpdateWindow();

}

void CRoadExtractionDoc::OnUpdateShow()
{
	// TODO: 在此添加命令处理程序代码
	if (!m_image.GetImage())
	{
		return;
	}

	UpdateShow();

	// 更新View
	UpdateAllViews(NULL);
	CMainFrame * pMainFrame = (CMainFrame*)AfxGetApp()->m_pMainWnd;  
	CRoadExtractionView * pView = (CRoadExtractionView*)pMainFrame->GetActiveView();  
	pView->UpdateWindow();
}

// 初始化演示功能
void CRoadExtractionDoc::InitShow()
{
	GeneratePointsForShow();

	//std::vector<int> path;
	//float distance = ComputeShortestPath(3,16, 
	//	path, cvScalar(0, 0, 0, 0), false);
	m_iCurrentM = m_iHotspotPerVehicle;


}


// 更新演示
void CRoadExtractionDoc::UpdateShow()
{

	// 将空的图像拷回,相当于重绘
	cvCopy(m_pImageCopy, m_image.GetImage(), NULL);

	// 道路结点数据
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




	// 如果m等于0了，就退出
	if (0 ==  m_iCurrentM)
	{


		m_vecAssignedHotspotForShow.clear();
		// 绘制所有热点
		for (int i = 0; i < m_vecHotspotForShow.size(); ++i)
		{
			cvCircle(m_image.GetImage(), m_vecHotspotForShow[i].point, 5, CV_RGB(255,0,0), 2);
		}

		// 绘制选中的m个热点
		for (int i = 0; i < m_vecAssignedHotspotForShow.size(); ++i)
		{
			cvCircle(m_image.GetImage(), m_vecAssignedHotspotForShow[i].point, 5, CV_RGB(0,255,0), 2);
		}

		// 绘制走过的路径
		if (m_vecPathDonedForShow.size() > 0)
		{
			for (int i = 0; i < m_vecPathDonedForShow.size() - 1; ++i)
			{
				cvLine(m_image.GetImage(), m_vecPathDonedForShow[i], m_vecPathDonedForShow[i + 1], CV_RGB(0, 50, 50), 2);
			}
		}


		// 绘制车辆
		cvCircle(m_image.GetImage(), m_vehicleForShow.point, 8, CV_RGB(0, 255, 0), -1);
		return;
	}


	
	// 搜索m最近个热点
	// 标记位
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


	// 计算距离
	// 标记是否计算已走过该热点
	flag.clear();
	for (int j = 0; j < m_vecAssignedHotspotForShow.size(); ++j)
	{
		flag.push_back(0);
	}
	int nextNodeTobeDelete = -1; // 记录下一个要到达的热点，即要被删除
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



	// 绘制

	// 绘制所有热点
	for (int i = 0; i < m_vecHotspotForShow.size(); ++i)
	{
		cvCircle(m_image.GetImage(), m_vecHotspotForShow[i].point, 5, CV_RGB(255,0,0), 2);
	}

	// 绘制选中的m个热点
	for (int i = 0; i < m_vecAssignedHotspotForShow.size(); ++i)
	{
		cvCircle(m_image.GetImage(), m_vecAssignedHotspotForShow[i].point, 5, CV_RGB(0,255,0), 2);
	}

	// 绘制走过的路径
	if (m_vecPathDonedForShow.size() > 0)
	{
		for (int i = 0; i < m_vecPathDonedForShow.size() - 1; ++i)
		{
			cvLine(m_image.GetImage(), m_vecPathDonedForShow[i], m_vecPathDonedForShow[i + 1], CV_RGB(0, 50, 50), 2);
		}
	}


	// 绘制车辆
	cvCircle(m_image.GetImage(), m_vehicleForShow.point, 8, CV_RGB(0, 255, 0), -1);




	// 计算下一刻会到达的热点，保存走过的路径
	std::vector<int> path;
	distance = ComputeShortestPath(m_vehicleForShow.no, m_vecAssignedHotspotForShow[nextNodeTobeDelete].no, 
		path, cvScalar(0, 0, 0, 0), false);

	for (int i = 0; i < path.size(); ++i)
	{
		m_vecPathDonedForShow.push_back(cvPoint(vecX[path[path.size() - i - 1]], vecY[path[path.size() - i - 1]]));
	}

	// 如果车辆是在途中的热点，将该热点标志为1
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
