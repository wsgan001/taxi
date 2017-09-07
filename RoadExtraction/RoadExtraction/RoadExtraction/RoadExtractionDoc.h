// RoadExtractionDoc.h : CRoadExtractionDoc ��Ľӿ�
//


#pragma once

// OpenCV ͷ�ļ�
#include <cv.h>
#include <highgui.h>
#include <vector>

#define NUM_VEHICLE 3
#define NUM_HOTSPOT 50	// ��˵��N
#define NUM_HOTSPOT_FOR_SHOW 15


class RoadPath;

typedef struct PointInfo{
	int no;
	CvPoint point;

} PointInfo;


class CRoadExtractionDoc : public CDocument
{
protected: // �������л�����
	CRoadExtractionDoc();
	DECLARE_DYNCREATE(CRoadExtractionDoc)

// ����
public:
	// ��ȡImageָ��
	CImage& GetImage();

// ����
public:

// ��д
public:
	virtual BOOL OnNewDocument();
	virtual void Serialize(CArchive& ar);
	virtual BOOL OnOpenDocument(LPCTSTR lpszPathName);
	virtual BOOL OnSaveDocument(LPCTSTR lpszPathName);

	void SetHotspotPerVehicle(int m);

// ʵ��
public:
	virtual ~CRoadExtractionDoc();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:

// ���ɵ���Ϣӳ�亯��
protected:
	DECLARE_MESSAGE_MAP()

private:

	afx_msg void OnRoadExtraction();
	afx_msg void OnPointGeneration();
	afx_msg void OnHotspotAssignment();
	afx_msg void OnShortestPath();
	afx_msg void OnInitializeShow();
	afx_msg void OnUpdateShow();

	// �������·��������·������
	float ComputeShortestPath(int srcX, int srcY, int dstX, int dstY, 
		CvScalar color = CV_RGB(255, 0, 0), bool isDrawPath = true, int thickness = 2);

	// �����յ��Ϊ��·�ڵ㣬�������·��
	float ComputeShortestPath(int srcIndex, int dstIndex, 
		CvScalar color = CV_RGB(255, 0, 0), bool isDrawPath = true,int thickness = 2);


	// ����·��������¼�����Ľڵ���Ϣ
	float ComputeShortestPath(int srcIndex, int dstIndex, std::vector<int>& path,
		CvScalar color = CV_RGB(255, 0, 0), bool isDrawPath = true, int thickness = 2);


	// ���Ƶ�·
	void DrawRoad();


	// ��������ȵ㼰������
	void GeneratePoint();

	// �����ȵ�
	void AssignHotspot();

	// ���㳵���˶������·��
	void ComputeVehiclePath();

	// ��ʼ����ʾ����
	void InitShow();


	// ������ʾ
	void UpdateShow();

	// Ϊ��ʾ���ɵ�
	void GeneratePointsForShow();


	//// �������ֱ�ߵľ���
	//float ComputeDistancePoint2Line(int px, int py, int linep1x, int linep1y, int linep2x, int linep2y);

	// ������ʾ��ͼ��
	CImage m_image;

	
	// �������·��
	RoadPath* m_pRoadPath;


	// �ȵ�
	std::vector<PointInfo> m_vecHotspot;


	// ����
	std::vector<PointInfo> m_vecVehicle;

	// ÿ�������ȵ�
	std::vector<PointInfo> m_hotspotPerVehicle[NUM_VEHICLE];


	// ��ͬ����ʹ�ò�ͬ��ɫ
	CvScalar m_colorVehicle[NUM_VEHICLE];

	int m_iHotspotPerVehicle;	// ��˵��m
	

	// ������ʾ�ı���
	IplImage* m_pImageCopy;	// ͼƬ�ĸ�������Ϊ�в�������������Ҫ�и����滻�ѻ��ƹ�������Ϣ��ͼƬ
	std::vector<PointInfo> m_vecHotspotForShow;	// �ȵ�
	std::vector<int> m_iFlagForShow;	// ����Ƿ��Ѿ������߹����ȵ�
	std::vector<PointInfo> m_vecAssignedHotspotForShow; // ��ѡ�е��ȵ�
	std::vector<CvPoint> m_vecPathDonedForShow;	// ���߹���·��
	std::vector<CvPoint> m_vecPathToDoForShow;	// ��Ҫ�ߵ�·��
	PointInfo m_vehicleForShow;	// ����
	int m_iCurrentM;		// ��ǰmֵ

	CvScalar m_colorNode;

};


