// RoadExtractionView.cpp : CRoadExtractionView 类的实现
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
	// 标准打印命令
	ON_COMMAND(ID_FILE_PRINT, &CView::OnFilePrint)
	ON_COMMAND(ID_FILE_PRINT_DIRECT, &CView::OnFilePrint)
	ON_COMMAND(ID_FILE_PRINT_PREVIEW, &CView::OnFilePrintPreview)
END_MESSAGE_MAP()

// CRoadExtractionView 构造/析构

CRoadExtractionView::CRoadExtractionView()
{
	// TODO: 在此处添加构造代码

}

CRoadExtractionView::~CRoadExtractionView()
{
}

BOOL CRoadExtractionView::PreCreateWindow(CREATESTRUCT& cs)
{
	// TODO: 在此处通过修改
	//  CREATESTRUCT cs 来修改窗口类或样式

	return CView::PreCreateWindow(cs);
}

// CRoadExtractionView 绘制

void CRoadExtractionView::OnDraw(CDC* pDC)
{
	CRoadExtractionDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	if (!pDoc)
		return;

	// TODO: 在此处为本机数据添加绘制代码
	CImage & img = pDoc->GetImage();
	CRect r;
	GetClientRect (&r);
	img.DrawToHDC(pDC->GetSafeHdc() ,r);
}


// CRoadExtractionView 打印

BOOL CRoadExtractionView::OnPreparePrinting(CPrintInfo* pInfo)
{
	// 默认准备
	return DoPreparePrinting(pInfo);
}

void CRoadExtractionView::OnBeginPrinting(CDC* /*pDC*/, CPrintInfo* /*pInfo*/)
{
	// TODO: 添加额外的打印前进行的初始化过程
}

void CRoadExtractionView::OnEndPrinting(CDC* /*pDC*/, CPrintInfo* /*pInfo*/)
{
	// TODO: 添加打印后进行的清除过程
}


// CRoadExtractionView 诊断

#ifdef _DEBUG
void CRoadExtractionView::AssertValid() const
{
	CView::AssertValid();
}

void CRoadExtractionView::Dump(CDumpContext& dc) const
{
	CView::Dump(dc);
}

CRoadExtractionDoc* CRoadExtractionView::GetDocument() const // 非调试版本是内联的
{
	ASSERT(m_pDocument->IsKindOf(RUNTIME_CLASS(CRoadExtractionDoc)));
	return (CRoadExtractionDoc*)m_pDocument;
}
#endif //_DEBUG


// CRoadExtractionView 消息处理程序
