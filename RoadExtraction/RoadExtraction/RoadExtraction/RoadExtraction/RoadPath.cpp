#include "stdafx.h"
#include "RoadPath.h"

#include <fstream>
#include <cmath>


#define INF 10000000.f;

RoadPath::RoadPath()
{

}


RoadPath::~RoadPath()
{

}


// 计算最短路径
float RoadPath::ComputeShortestPath(int srcNode, int dstNode, std::vector<int>& shortPath)
{
	float miniDistance = 0.f;
	// 读入节点数据
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

	// 构造邻接矩阵
	float** adjcentMatrix = new float* [vecX.size()];
	for (int i = 0; i < vecX.size(); ++i)
	{
		adjcentMatrix[i] = new float [vecX.size()];
		for (int j = 0; j < vecX.size(); ++j)
		{
			adjcentMatrix[i][j] = INF;
		}
	}
	int numAdjacentNodes, indexAdjacentNode;
	inputFileStream.open("..\\bin\\notes_adjacency.txt");
	for (int i = 0; i < vecX.size(); ++i)
	{
		inputFileStream >> indexAdjacentNode >> numAdjacentNodes;
		for (int j = 0; j < numAdjacentNodes; ++j)
		{
			inputFileStream >> indexAdjacentNode;
			adjcentMatrix[i][indexAdjacentNode] = sqrt(1.f *pow(1.0f * vecX[i] - vecX[indexAdjacentNode], 2) + 
				pow(1.0f * vecY[i] - vecY[indexAdjacentNode], 2));
		}
	}
	inputFileStream.close();
	inputFileStream.clear();


	//std::ofstream outputFileStream;
	//outputFileStream.open("matrix.txt");
	//for (int i = 0; i < vecX.size(); ++i)
	//{
	//	for (int j = 0; j < vecX.size(); ++j)
	//	{
	//		outputFileStream << adjcentMatrix[i][j] << " ";
	//	}
	//	outputFileStream << std::endl;
	//}
	//outputFileStream.close();
	//outputFileStream.clear();


	//这个数组存放各顶点的信息，主要是在不在已经选入的集合中，以及起始点到本点的长度信息
	PNODE roadNodes = new NODE [vecX.size()];


	//初始化：为没有直接路径的点赋权为inf-无穷大
	for (int i = 0; i < vecX.size(); ++i)
	{
		roadNodes[i].no = i;    //各节点的编号
		roadNodes[i].inflag   = 0; //不在已选入的最短路径结点集合中
		roadNodes[i].wtlen    = INF;
	}

	roadNodes[srcNode].no    = srcNode;
	roadNodes[srcNode].wtlen = 0;       //这里跟上面循环不一样，因为点start为起点，到自己距离为0
	roadNodes[srcNode].inflag = 1;		//选入到已求得最短路径的点集中
	roadNodes[srcNode].pre = NULL;		//它前面没有点，故设为NULL

	int count=1, minv, minindx; //minv为每次搜索到的最小路径值，minindx为对应点的编号
	PNODE p;
	int tempi = srcNode;
	float x1, x2;
	while(count < vecX.size())
	{
		minv = INF;
		for(int j = 0;j <= vecX.size(); ++j) //在未搜索过的结点集中搜索距离最小的结点
		{
			if((roadNodes[j].inflag == 0) && (j!=tempi)) //在未搜索过的结点集中搜索距离最小的结点
			{
				x1 = adjcentMatrix[tempi][j]+roadNodes[tempi].wtlen ;  //i->j的路径长
				x2 = roadNodes[j].wtlen ;          //结点j原来保存的路径长
				if (x1>x2)        //这两个值中找最小的一个，来更新对应的结点信息
				{
					roadNodes[j].wtlen = x2;
				}else
				{
					roadNodes[j].wtlen = x1;
					roadNodes[j].pre = &roadNodes[tempi]; //★修改和记录结点j的前一个结点（打印路径用）
				}
				//求最小距离及对应的结点编号
				if( minv > roadNodes[j].wtlen)
				{
					minv = roadNodes[j].wtlen;
					minindx = j;
				}
			}
		}

		roadNodes[minindx].inflag = 1; //把最小路径对应结点选入到最小距离的点集中
		tempi=minindx;//下一次从本次找到的最小路径对应结点开始
		count++;  //计数器，保证找遍n-1个其他结点到start结点的最小距离

		if (minindx == dstNode)
		{
			miniDistance = minv;
			p = roadNodes + minindx;
			while (p!= NULL)
			{
				shortPath.push_back(p->no);
				p = p->pre;
			}

			break;
		}

	}


	for (int i = 0; i < vecX.size(); ++i)
	{
		delete [] adjcentMatrix[i];
	}
	delete [] adjcentMatrix;
	delete [] roadNodes;

	return miniDistance;

}