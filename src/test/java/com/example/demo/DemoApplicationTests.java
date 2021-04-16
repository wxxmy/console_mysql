package com.example.demo;

import com.example.demo.domain.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Test
	void contextLoads() {
		List<Integer> list = Arrays.asList(1, 10, 3, 7, 5);
		int a = list.stream()
				.peek(num -> System.out.println("will filter " + num))
				.filter(x -> x > 5)
				.findFirst()
				.get();
		System.out.println(a);
	}

	@Test
	void testCreateIndex() throws IOException {
		CreateIndexRequest createIndexRequest = new CreateIndexRequest("xu_mingyang");

		CreateIndexResponse response = restHighLevelClient.indices().create(createIndexRequest,
				RequestOptions.DEFAULT);

		System.out.println("response = " + response);
	}

	@Test
	void isExsist() throws IOException {
		GetIndexRequest getIndexRequest = new GetIndexRequest("xu_mingyang");

		boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);

		System.out.println("exists = " + exists);
	}

	@Test
	void deleteIndex() throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest("xu_mingyang");
		AcknowledgedResponse delete = restHighLevelClient.indices()
				.delete(request, RequestOptions.DEFAULT);
		System.out.println("delete = " + delete.isAcknowledged());
	}

	@Test
	void getIndex() throws IOException {
		GetIndexRequest request = new GetIndexRequest("test1");

		GetIndexResponse response = restHighLevelClient.indices()
				.get(request, RequestOptions.DEFAULT);

		System.out.println("response.getMappings() = " + response.getMappings());
	}

	/*测试差集*/
	@Test
	void testChaJi() {
		User user1 = new User(1, "1", 1, "1");
		User user2 = new User(2, "2", 2, "2");
		User user22 = new User(2, "张三", 23, "男");
		User user3 = new User(3, "3", 3, "3");
		User user33 = new User(3, "李四", 24, "女");
		User user4 = new User(4, "4", 4, "4");
		User user44 = new User(4, "王五", 35, "男");
		User user5 = new User(5, "5", 5, "5");
		List<User> list1 = new ArrayList<>();
		list1.add(user2);
		list1.add(user1);
		list1.add(user4);
		list1.add(user3);
		list1.add(user5);

		List<User> list2 = new ArrayList<>();
		list2.add(user44);
		list2.add(user22);
		list2.add(user33);

		// 差集 (list2 - list1)
		List<User> removeAll = removeAll(list1, list2);
		System.out.println("removeAll = " + removeAll);
	}

	/**
	 * 差集：删除左边集合中在右边集合存在的元素并返回
	 *
	 * @param left 大集合
	 * @param right 小集合
	 * @return List<User>
	 */
	private static List<User> removeAll(List<User> left, List<User> right) {
		//使用LinkedList方便插入和删除
		List<User> res = new LinkedList<>(left);
		Set<Integer> set = new HashSet<>();
		//将User id存放到set
		for (User item : right) {
			set.add(item.getId());
		}
		res.removeIf(item -> set.contains(item.getId()));
		right.addAll(res);
		return right.stream().sorted(Comparator.comparing(User::getId))
				.collect(Collectors.toList());
	}

}
