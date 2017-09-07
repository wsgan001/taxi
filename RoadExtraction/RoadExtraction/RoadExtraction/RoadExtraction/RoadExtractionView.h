// RoadExtractionView.h : CRoadExtractionView ��Ľӿ�
//


#pragma once


class CRoadExtractionView : public CView
{
protected: // �������л�����
	CRoadExtractionView();
	DECLARE_DYNCREATE(CRoadExtractionView)

// ����
public:
	CRoadExtractionDoc* GetDocument() const;

// ����
public:

// ��д
public:
	virtual void OnDraw(CDC* pDC);  // ��д�Ի��Ƹ���ͼ
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);
protected:
	virtual BOOL OnPreparePrinting(CPrintInfo* pInfo);
	virtual void OnBeginPrinting(CDC* pDC, CPrintInfo* pInfo);
	virtual void OnEndPrinting(CDC* pDC, CPrintInfo* pInfo);

// ʵ��
public:
	virtual ~CRoadExtractionView();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:

// ���ɵ���Ϣӳ�亯��
protected:
	DECLARE_MESSAGE_MAP()
};

#ifndef _DEBUG  // RoadExtractionView.cpp �еĵ��԰汾
inline CRoadExtractionDoc* CRoadExtractionView::GetDocument() const
   { return reinterpret_cast<CRoadExtractionDoc*>(m_pDocument); }
#endif

