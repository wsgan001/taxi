// RoadExtractionDoc.cpp : CRoadExtractionDoc 类的实现
//

#include "stdafx.h"
#include "RoadExtraction.h"

#include "RoadExtractionDoc.h"


#include <fstream>
#include <cmath>
#include <vector>
#include <cv.h>

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
END_MESSAGE_MAP()


// CRoadExtractionDoc 构造/析构

CRoadExtractionDoc::CRoadExtractionDoc()
{
	// TODO: 在此添加一次性构造代码

	m_pRoadPath = new RoadPath();

}

CRoadExtractionDoc::~CRoadExtractionDoc()
{

	//delete m_pImage;
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
	//DrawRoad();

	// 计算路径距离
	float distance = ComputeShortestPath(25, 178, 680, 400);


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
			cvLine(m_image.GetImage(), cvPoint(vecX[i], vecY[i]), cvPoint(vecX[index], vecY[index]), CV_RGB(255,0,0), 2);
		}
	}
	inputFileStream.close();
	inputFileStream.clear();
}

float CRoadExtractionDoc::ComputeShortestPath(int srcX, int srcY, int dstX, int dstY)
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
	for (int i = 0; i < shortestPath.size() - 1; ++i)
	{
		cvLine(m_image.GetImage(), cvPoint(vecX[shortestPath[i]], vecY[shortestPath[i]]), 
			cvPoint(vecX[shortestPath[i + 1]], vecY[shortestPath[i + 1]]), CV_RGB(255,0,0), 2);
	}
	if (shortestPath.size() > 0)
	{	
		cvLine(m_image.GetImage(), cvPoint(vecX[shortestPath[0]], vecY[shortestPath[0]]), 
			cvPoint(dstX, dstY), CV_RGB(255,0,0), 2);
		cvLine(m_image.GetImage(), cvPoint(vecX[shortestPath[shortestPath.size() -1]], vecY[shortestPath[shortestPath.size() -1]]), 
			cvPoint(srcX, srcY), CV_RGB(255,0,0), 2);

	}
	return distance;
}

