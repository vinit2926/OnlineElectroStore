package com.electronicstore;

import com.electronicstore.entities.Role;
import com.electronicstore.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.UUID;

@SpringBootApplication
public class ElectronicStoreApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicStoreApplication.class, args);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Value("${normal.role.id}")
	private String normalRoleId;

	@Value("${admin.role.id}")
	private String adminRoleId;

	@Override
	public void run(String... args) throws Exception {
		System.out.println(passwordEncoder.encode("1234"));


		try{

			Role admin = Role.builder()
					.roleId(adminRoleId)
					.roleName("ADMIN")
					.build();

			Role normal = Role.builder()
					.roleId(normalRoleId)
					.roleName("NORMAL")
					.build();

			roleRepository.save(admin);
			roleRepository.save(normal);
		}
		catch(Exception e){

		}
	}

}
