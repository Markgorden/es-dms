package test.github.richardwilly98.services;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.Role;
import com.github.richardwilly98.api.services.PermissionService;

public class RoleProviderTest extends ProviderTestBase {

	private String testCreateRole(String name, String description, /*Set<Permission> permissions,*/Map<String, Permission> permissions, boolean disabled) throws Throwable {
		
		Role role = createRole(name, description, disabled, permissions);
		
		log.trace("role created: " + role.getId());
		Assert.assertNotNull(role);
		log.trace("role name: " + role.getName());
		Assert.assertEquals(name, role.getName());
		log.trace("role description: " + role.getDescription());
		Assert.assertEquals(description, role.getDescription());
		log.trace("role disabled: " + role.isDisabled());
		Assert.assertEquals(disabled, role.isDisabled());
//		Assert.assertEquals(permissions, role.getPermissions());

		return role.getId();
	}

	@Test
	public void testCreateRole() throws Throwable {
		log.info("Start testCreateRole");
		
//		Set<Permission> permissions = new HashSet<Permission>();
		Map<String, Permission> permissions = new HashMap<String, Permission>();
		
		permissions.put("profile:read", createPermission("profile:read", "profile:read", true));
		permissions.put("content:read", createPermission("content:read", "content:read", true));
		permissions.put("annotation:read", createPermission("annotation:read", "annotation:read", true));
		permissions.put("annotation:write", createPermission("annotation:write", "annotation:write", true));
		permissions.put("comment:read", createPermission("comment:read", "comment:read", true));
		permissions.put("comment:write", createPermission("comment:write", "comment:write", true));
		permissions.put("content:todelete", createPermission("content:todelete", "content:todelete", true));
		testCreateRole("Proof-Reader", "reader", permissions, false);
		log.info("Proof-Reader permissions count: " + permissions.size());
		Assert.assertEquals(permissions.size(), 7);
		permissions.put("profile:write", createPermission("profile:write", "profile:write", true));
		permissions.put("content:write", createPermission("content:write", "content:write", true));
		permissions.put("content:add", createPermission("content:add", "content:add", true));
		permissions.put("content:remove", createPermission("content:remove", "content:remove", true));
		permissions.put("profile:todelete", createPermission("profile:todelete", "profile:todelete", true));
		testCreateRole("Writer", "writer", permissions, false);
		log.info("Writer permissions count: " + permissions.size());
		Assert.assertEquals(permissions.size(), 12);
		permissions.put("user:add", createPermission("user:add", "user:add", true));
		permissions.put("user:remove", createPermission("user:remove", "user:remove", true));
		permissions.put("group:add", createPermission("group:add", "group:add", true));
		permissions.put("group:remove", createPermission("group:remove", "group:remove", true));
		permissions.put("role:add", createPermission("role:add", "role:add", true));
		permissions.put("role:remove", createPermission("role:remove", "role:remove", true));
		permissions.put("milestone:add", createPermission("milestone:add", "milestone:add", true));
		permissions.put("milestone:remove", createPermission("milestone:remove", "milestone:remove", true));
		permissions.put("task:assign", createPermission("task:assign", "task:assign", true));
		testCreateRole("Coordinator", "coordinator", permissions, false);
		log.info("Coordinator permissions count: " + permissions.size());
		Assert.assertEquals(permissions.size(), 21);
		
		log.info("Start testCreateRole completed!!");
	}
	
//	@Test
//	public void testFindUser() throws Throwable {
//		log.info("Start testFindUser");
//		
//		UserService provider = getUserProvider();
//		User user = provider.get("Richard");
//		
//		Assert.assertNotNull(user);
//		if (!(user == null) )log.info("User found: " + user.getName());
//		
//		user = provider.get("Danilo");
//		
//		//Assert.assertNotNull(user);
//		if (!(user == null))log.info("User found: " + user.getName());
//	}
//	
//	@Test
//	public void testAddUserRole() throws Throwable {
//		log.info("Start testAddUserRole");
//		UserService provider = getUserProvider();
//		User user = provider.get("Richard");
//		
//		
//	}
//
//	@Test
//	public void testDeleteUser() throws Throwable {
//		log.info("Start testDeleteUser");
//		String id = testCreateUser("Richard", "Lead developer", false,
//				"richard@pippo.pippo", null);
//		UserService provider = getUserProvider();
//		User user = provider.get(id);
//		provider.delete(user);
//		user = provider.get(id);
//		Assert.assertNull(user);
//	}
//	
//	@Test
//	public void testListUser() throws Throwable {
//		String id1 = testCreateUser("Danilo1", "Lead developer", false,
//				"richard@pippo.pippo", null);
//		String id2 = testCreateUser("Danilo2", "Mezza calzetta", true, "danilo@pippo.pippo", null);
//		UserService provider = getUserProvider();
//		List<User> users = provider.getList("Danilo");
//		Assert.assertNotNull(users);
//		int found = 0;
//		log.debug(String.format("id1 %s", id1));
//		log.debug(String.format("id2 %s", id2));
//		for (User user : users) {
//			log.debug(String.format("User %s", user.getId()));
//			if (id1.equals(user.getId()) || id2.equals(user.getId())) {
//				found++;
//			}
//		}
//		Assert.assertEquals(found, 2);
//	}
}
