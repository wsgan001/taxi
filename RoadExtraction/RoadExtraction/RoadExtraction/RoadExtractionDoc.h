// RoadExtractionDoc.h : CRoadExtractionDoc 类的接口
//


#pragma once

// OpenCV 头文件
#include <cv.h>
#include <highgui.h>
#include <vector>

#define NUM_VEHICLE 3
#define NUM_HOTSPOT 50	// 你说的N
#define NUM_HOTSPOT_FOR_SHOW 15


class RoadPath;

typedef struct PointInfo{
	int no;
	CvPoint point;

} PointInfo;


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

	void SetHotspotPerVehicle(int m);

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
	afx_msg void OnPointGeneration();
	afx_msg void OnHotspotAssignment();
	afx_msg void OnShortestPath();
	afx_msg void OnInitializeShow();
	afx_msg void OnUpdateShow();

	// 计算最短路径，返回路径长度
	float ComputeShortestPath(int srcX, int srcY, int dstX, int dstY, 
		CvScalar color = CV_RGB(255, 0, 0), bool isDrawPath = true, int thickness = 2);

	// 起点和终点均为道路节点，计算最短路径
	float ComputeShortestPath(int srcIndex, int dstIndex, 
		CvScalar color = CV_RGB(255, 0, 0), bool isDrawPath = true,int thickness = 2);


	// 计算路径，并记录经过的节点信息
	float ComputeShortestPath(int srcIndex, int dstIndex, std::vector<int>& path,
		CvScalar color = CV_RGB(255, 0, 0), bool isDrawPath = true, int thickness = 2);


	// 绘制道路
	void DrawRoad();


	// 生成随机热点及车辆点
	void GeneratePoint();

	// 分配热点
	void AssignHotspot();

	// 计算车辆运动的最短路径
	void ComputeVehiclePath();

	// 初始化演示功能
	void InitShow();


	// 更新演示
	void UpdateShow();

	// 为演示生成点
	void GeneratePointsForShow();


	//// 计算点与直线的距离
	//float ComputeDistancePoint2Line(int px, int py, int linep1x, int linep1y, int linep2x, int linep2y);

	// 用于显示的图像
	CImage m_image;

	
	// 计算最短路径
	RoadPath* m_pRoadPath;


	// 热点
	std::vector<PointInfo> m_vecHotspot;


	// 车辆
	std::vector<PointInfo> m_vecVehicle;

	// 每辆车的热点
	std::vector<PointInfo> m_hotspotPerVehicle[NUM_VEHICLE];


	// 不同车辆使用不同颜色
	CvScalar m_colorVehicle[NUM_VEHICLE];

	int m_iHotspotPerVehicle;	// 你说的m
	

	// 用于演示的变量
	IplImage* m_pImageCopy;	// 图片的副本，因为有擦除操作，所以要有副本替换已绘制过其他信息的图片
	std::vector<PointInfo> m_vecHotspotForShow;	// 热点
	std::vector<int> m_iFlagForShow;	// 标记是否已经做过走过该热点
	std::vector<PointInfo> m_vecAssignedHotspotForShow; // 被选中的热点
	std::vector<CvPoint> m_vecPathDonedForShow;	// 已走过的路线
	std::vector<CvPoint> m_vecPathToDoForShow;	// 将要走的路线
	PointInfo m_vehicleForShow;	// 车辆
	int m_iCurrentM;		// 当前m值

	CvScalar m_colorNode;

};


