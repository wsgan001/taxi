// RoadExtractionDoc.h : CRoadExtractionDoc 类的接口
//


#pragma once

// OpenCV 头文件
#include <highgui.h>

#define NUM_TAG_COLOR 7


class RoadPath;

class CRoadExtractionDoc : public CDocument
{
protected: // 仅从序列化创建
	CRoadExtractionDoc();
	DECLARE_DYNCREATE(CRoadExtractionDoc)

// 属性
public:
	// 获取Image指针
	CImage& GetImage();

// 操作
public:

// 重写
public:
	virtual BOOL OnNewDocument();
	virtual void Serialize(CArchive& ar);
	virtual BOOL OnOpenDocument(LPCTSTR lpszPathName);
	virtual BOOL OnSaveDocument(LPCTSTR lpszPathName);

// 实现
public:
	virtual ~CRoadExtractionDoc();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:

// 生成的消息映射函数
protected:
	DECLARE_MESSAGE_MAP()

private:

	afx_msg void OnRoadExtraction();


	// 计算最短路径，放回路径长度
	float ComputeShortestPath(int srcX, int srcY, int dstX, int dstY);

	// 绘制道路
	void DrawRoad();

	//// 计算点与直线的距离
	//float ComputeDistancePoint2Line(int px, int py, int linep1x, int linep1y, int linep2x, int linep2y);

	// 用于显示的图像
	CImage m_image;

	
	// 计算最短路径
	RoadPath* m_pRoadPath;



	
};


