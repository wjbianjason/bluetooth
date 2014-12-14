/**
* [use find_pair() to find all pairs of edges, which are diverse structure patterns with equivalent semantics.]
* The following is global varibles:
* @var	 [map]	entity_class	   [entity_class maps each entity’s id to its class’s id]
* @var	 [map]	pre_id_name     [pre_id_name maps each predicate's id to its name]
* @var	 [map]	edge_1		   [edge_1 maps each edge’s hash_code and predicate’s id to predicate's name when class_id1≤class_id2]
* @var	 [map] 	edge_2		   [edge_2 maps each edge’s hash_code and predicate’s id to predicate's name when class_id1≥class_id2]
* @var   [map] 	class_id_name   [class_id_name maps id of one class to its name]
* @var   [map] 	mappredicate	   [mappredicate maps predicates' names to their fathers and sons, which is stored in pre_relation*]
*/
void find_pair()
{
	fstream fin,fout;
    fin.open("id-id-predicates.txt",ios::in);//id-id-predicates.txt contains edges between entities and the predicates of the edge
	fout.open("results.txt",ios::out);//use results.txt to store pairs of edges meeting requirements
	int entity1_id,entity2_id;		//ids of two entities of one edge
    int	class1_id,class2_id;		//ids of the two entities’ classes
    int predicate_id;	       		//id of an edge’s predicate
    string pre_name;				//predicate’s name
	while(!fin.eof())
	{
		fin>>entity1_id>>entity2_id>>predicate_id;
    	class1_id = entity_class[entity1_id]<entity_class[entity2_id]?entity_class[entity1_id]:entity_class[entity2_id]; 
		class2_id = entity_class[entity1_id]<entity_class[entity2_id]?entity_class[entity2_id]:entity_class[entity1_id];
		pre_name = pre_id_name[predicate_id];
		int hash_code = class1_id * 1000 + class2_id;//store ids of class1 and class2 by coding them together
	  	if(class1_id == class2_id)
	  	{
			edge_1[hash_code][predicate_id] = pre_name;
			edge_2[hash_code][predicate_id] = pre_name;
		}
		else
		{
			if(entity_class[entity1_id]<entity_class[entity2_id])
				edge_1[hash_code][predicate_id] = pre_name;
			else
				edge_2[hash_code][predicate_id] = pre_name;
		}
	}

	for(map<int,map<int,string>>::iterator it1 = edge_1.begin();it1 != edge_1.end();it1++)
	{ 
		int hash_code = it1->first;
		class1_id = hash_code/1000;
		class2_id = hash_code%1000;
		string str_1 = class_id_name[class_id1];		
		string str_2 = class_id_name[class_id2];
		map<int,map<int,string>>::iterator it_2=edge_2.find(hash_code);		
		if(it_2 == edge_2.end())
			continue;
		else 
		{
			if(it1->second.size()!=0&&it_2->second.size()!=0)
			{
				for(map<int,string>::iterator it3=it1->second.begin();it3 != it1->second.end();it3++)
				{ 
					string pre1,pre2;//pre1,pre2 are names  of edges' predicates
					pre1 = it3->second;		
					for(map<int,string>::iterator it4 = edge_2[hash_code].begin(); it4 != edge_2[hash_code].end();it4++)
					{
						pre2 = it4->second;		
					  	father_intersect.clear();//father_intersect = father1 ∩ father2 
						father_join.clear();	//father_join = father1 ∪ father2
						bool flag1 = similar_check(pre1,pre2);
						if(flag1)
							fout<<str_1<<" "<<str_2<<"     "<<pre1<<" "<<pre2<<endl;
					}	
				}	
			}	
		}	
	}
	fin.close();
	fout.close();
}
/**
* [similar_check(string pre1, string pre2) is used to check the semantic similarity of pre1and pre2]		
* @var	  [vector]   father1 	[father1 is used to store all of predicate1's father in the predicate ontology]
* @var	  [vector]   father2 	[father2 is used to store all of predicate2's father in the predicate ontology]
* @param  [string]	pre1		[predicate1’s name]
* @param  [string]    pre2		[predicate2’s name]
*/
bool similar_check(string pre1,string pre2)
{
	if(pre1==pre2||it1->second->name->father_name.size()==0||it2->second->name->father_name.size()==0)
		return false;
	map<string, pre_relation*>::iterator it1 = mappredicate.find(pre1);		
    map<string, pre_relation*>::iterator it2 = mappredicate.find(pre2);
	father1.clear();		
	father2.clear();				
	find_father(it1->second->name->father_name,father1);// find_father() uses father1 to store pre1's father
	find_father(it2->second->name->father_name,father2);		
	intersect_join(father1,father2);//intersect_join() computes intersections and unions of father2 and father2
 	int sum_intersec = father_intersect.size();
	int sum_join = father_join.size();
	double tmp_threshold = 1-sum_inter/sum_join;
	if(tmp_threshold < threshold)
		return true;
	else
		return false;
}




