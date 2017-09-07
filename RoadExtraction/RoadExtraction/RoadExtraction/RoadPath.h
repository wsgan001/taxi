#ifndef ROAD_PATH_H_
#define ROAD_PATH_H_

#include <vector>

//������Ϣ�ṹ��
typedef struct node{
	int no;     //�����
	float wtlen;    //��Ȩ·������
	int inflag;    //�Ƿ��Ѿ�ѡ�뵽���·���ĵ�ļ����У���Ϊ1����Ϊ0
	struct node * pre;  //ָ��ǰһ���ӵ㡣������ʼ�ڵ㣬��ΪNULL��Ҳ�ɸ�Ϊ���ͱ�ţ�
} NODE, *PNODE;


class RoadPath
{
public:
	RoadPath();
	~RoadPath();

	//  �������·��
	float ComputeShortestPath(int srcNode, int dstNode, std::vector<int>& shortPath);
};

#endif // ROAD_PATH_H_