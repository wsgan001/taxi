// RoadExtractionDoc.h : CRoadExtractionDoc ��Ľӿ�
//


#pragma once

// OpenCV ͷ�ļ�
#include <highgui.h>

#define NUM_TAG_COLOR 7


class RoadPath;

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


	// �������·�����Ż�·������
	float ComputeShortestPath(int srcX, int srcY, int dstX, int dstY);

	// ���Ƶ�·
	void DrawRoad();

	//// �������ֱ�ߵľ���
	//float ComputeDistancePoint2Line(int px, int py, int linep1x, int linep1y, int linep2x, int linep2y);

	// ������ʾ��ͼ��
	CImage m_image;

	
	// �������·��
	RoadPath* m_pRoadPath;



	
};


