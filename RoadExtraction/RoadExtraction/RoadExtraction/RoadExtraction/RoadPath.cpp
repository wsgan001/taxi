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


// �������·��
float RoadPath::ComputeShortestPath(int srcNode, int dstNode, std::vector<int>& shortPath)
{
	float miniDistance = 0.f;
	// ����ڵ�����
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

	// �����ڽӾ���
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


	//��������Ÿ��������Ϣ����Ҫ���ڲ����Ѿ�ѡ��ļ����У��Լ���ʼ�㵽����ĳ�����Ϣ
	PNODE roadNodes = new NODE [vecX.size()];


	//��ʼ����Ϊû��ֱ��·���ĵ㸳ȨΪinf-�����
	for (int i = 0; i < vecX.size(); ++i)
	{
		roadNodes[i].no = i;    //���ڵ�ı��
		roadNodes[i].inflag   = 0; //������ѡ������·����㼯����
		roadNodes[i].wtlen    = INF;
	}

	roadNodes[srcNode].no    = srcNode;
	roadNodes[srcNode].wtlen = 0;       //���������ѭ����һ������Ϊ��startΪ��㣬���Լ�����Ϊ0
	roadNodes[srcNode].inflag = 1;		//ѡ�뵽��������·���ĵ㼯��
	roadNodes[srcNode].pre = NULL;		//��ǰ��û�е㣬����ΪNULL

	int count=1, minv, minindx; //minvΪÿ������������С·��ֵ��minindxΪ��Ӧ��ı��
	PNODE p;
	int tempi = srcNode;
	float x1, x2;
	while(count < vecX.size())
	{
		minv = INF;
		for(int j = 0;j <= vecX.size(); ++j) //��δ�������Ľ�㼯������������С�Ľ��
		{
			if((roadNodes[j].inflag == 0) && (j!=tempi)) //��δ�������Ľ�㼯������������С�Ľ��
			{
				x1 = adjcentMatrix[tempi][j]+roadNodes[tempi].wtlen ;  //i->j��·����
				x2 = roadNodes[j].wtlen ;          //���jԭ�������·����
				if (x1>x2)        //������ֵ������С��һ���������¶�Ӧ�Ľ����Ϣ
				{
					roadNodes[j].wtlen = x2;
				}else
				{
					roadNodes[j].wtlen = x1;
					roadNodes[j].pre = &roadNodes[tempi]; //���޸ĺͼ�¼���j��ǰһ����㣨��ӡ·���ã�
				}
				//����С���뼰��Ӧ�Ľ����
				if( minv > roadNodes[j].wtlen)
				{
					minv = roadNodes[j].wtlen;
					minindx = j;
				}
			}
		}

		roadNodes[minindx].inflag = 1; //����С·����Ӧ���ѡ�뵽��С����ĵ㼯��
		tempi=minindx;//��һ�δӱ����ҵ�����С·����Ӧ��㿪ʼ
		count++;  //����������֤�ұ�n-1��������㵽start������С����

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