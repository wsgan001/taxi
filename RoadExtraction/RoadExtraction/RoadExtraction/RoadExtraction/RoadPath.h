#ifndef ROAD_PATH_H_
#define ROAD_PATH_H_

#include <vector>

//顶点信息结构体
typedef struct node{
	int no;     //结点编号
	float wtlen;    //带权路径长度
	int inflag;    //是否已经选入到最短路径的点的集合中，是为1，否为0
	struct node * pre;  //指向前一个接点。若是起始节点，则为NULL（也可改为整型编号）
} NODE, *PNODE;


class RoadPath
{
public:
	RoadPath();
	~RoadPath();

	//  计算最短路径
	float ComputeShortestPath(int srcNode, int dstNode, std::vector<int>& shortPath);
};

#endif // ROAD_PATH_H_