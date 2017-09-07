// RoadExtractionView.cpp : CRoadExtractionView ���ʵ��
//

#include "stdafx.h"
#include "RoadExtraction.h"

#include "RoadExtractionDoc.h"
#include "RoadExtractionView.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CRoadExtractionView

IMPLEMENT_DYNCREATE(CRoadExtractionView, CView)

BEGIN_MESSAGE_MAP(CRoadExtractionView, CView)
	// ��׼��ӡ����
	ON_COMMAND(ID_FILE_PRINT, &CView::OnFilePrint)
	ON_COMMAND(ID_FILE_PRINT_DIRECT, &CView::OnFilePrint)
	ON_COMMAND(ID_FILE_PRINT_PREVIEW, &CView::OnFilePrintPreview)
END_MESSAGE_MAP()

// CRoadExtractionView ����/����

CRoadExtractionView::CRoadExtractionView()
{
	// TODO: �ڴ˴���ӹ������

}

CRoadExtractionView::~CRoadExtractionView()
{
}

BOOL CRoadExtractionView::PreCreateWindow(CREATESTRUCT& cs)
{
	// TODO: �ڴ˴�ͨ���޸�
	//  CREATESTRUCT cs ���޸Ĵ��������ʽ

	return CView::PreCreateWindow(cs);
}

// CRoadExtractionView ����

void CRoadExtractionView::OnDraw(CDC* pDC)
{
	CRoadExtractionDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	if (!pDoc)
		return;

	// TODO: �ڴ˴�Ϊ����������ӻ��ƴ���
	CImage & img = pDoc->GetImage();
	CRect r;
	GetClientRect (&r);
	img.DrawToHDC(pDC->GetSafeHdc() ,r);
}


// CRoadExtractionView ��ӡ

BOOL CRoadExtractionView::OnPreparePrinting(CPrintInfo* pInfo)
{
	// Ĭ��׼��
	return DoPreparePrinting(pInfo);
}

void CRoadExtractionView::OnBeginPrinting(CDC* /*pDC*/, CPrintInfo* /*pInfo*/)
{
	// TODO: ��Ӷ���Ĵ�ӡǰ���еĳ�ʼ������
}

void CRoadExtractionView::OnEndPrinting(CDC* /*pDC*/, CPrintInfo* /*pInfo*/)
{
	// TODO: ��Ӵ�ӡ����е��������
}


// CRoadExtractionView ���

#ifdef _DEBUG
void CRoadExtractionView::AssertValid() const
{
	CView::AssertValid();
}

void CRoadExtractionView::Dump(CDumpContext& dc) const
{
	CView::Dump(dc);
}

CRoadExtractionDoc* CRoadExtractionView::GetDocument() const // �ǵ��԰汾��������
{
	ASSERT(m_pDocument->IsKindOf(RUNTIME_CLASS(CRoadExtractionDoc)));
	return (CRoadExtractionDoc*)m_pDocument;
}
#endif //_DEBUG


// CRoadExtractionView ��Ϣ�������
