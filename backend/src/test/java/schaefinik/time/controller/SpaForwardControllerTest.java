//package schaefinik.time.controller;
//
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
//
//@WebMvcTest(SpaForwardController.class)
//@EnableMethodSecurity
//@AutoConfigureDataJpa
//@ActiveProfiles("test")
//public class SpaForwardControllerTest {
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	@Nested
//	class SuccessCases {
//
//		@Test
//		public void forwardSpaRoutes() throws Exception {
//			mockMvc.perform(get(""))
//					.andExpect(status().isOk())
//					.andExpect(view().name("forward:index.html"));
//		}
//
//
//		@Test
//		public void forwardSpaRoutes_timeEntries() throws Exception {
//			mockMvc.perform(get("/time-entries"))
//					.andExpect(status().isOk())
//					.andExpect(view().name("forward:/index.html"));
//		}
//
//		@Test
//		public void forwardSpaRoutes_login() throws Exception {
//			mockMvc.perform(get("/login"))
//					.andExpect(status().isOk())
//					.andExpect(view().name("forward:/index.html"));
//		}
//
//
//		@Test
//		public void forwardSpaRoutes_admin() throws Exception {
//			mockMvc.perform(get("/admin"))
//					.andExpect(status().isOk())
//					.andExpect(view().name("forward:/index.html"));
//		}
//
//	}
//
//	@Nested
//	class FailCases {
//
//		@Test
//		public void forwardSpaRoutes() throws Exception {
//			mockMvc.perform(get("/no-page"))
//					.andExpect(status().isUnauthorized());
//		}
//	}
//
//}
