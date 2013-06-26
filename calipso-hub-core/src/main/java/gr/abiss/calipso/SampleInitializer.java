package gr.abiss.calipso;

import gr.abiss.calipso.model.Sample;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.repository.SampleRepository;
import gr.abiss.calipso.service.UserService;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.resthub.common.util.PostInitialize;

@Named("sampleInitializer")
public class SampleInitializer {

    @Inject
    @Named("sampleRepository")
    private SampleRepository sampleRepository;

	@Inject
	@Named("userService")
	private UserService userService;

    @PostInitialize
    public void init() {
        sampleRepository.save(new Sample("testSample1"));
        sampleRepository.save(new Sample("testSample2"));
        sampleRepository.save(new Sample("testSample3"));

		Date now = new Date();

		User u0 = new User("info@abiss.gr");
		u0.setFirstName("admin");
		u0.setLastName("user");
		u0.setUserName("admin");
		u0.setUserPassword("admin");
		u0.setLastVisit(now);
		u0 = userService.create(u0);

		User u1 = new User("manos@abiss.gr");
		u1.setFirstName("Manos");
		u1.setLastName("Batsis");
		u1.setUserName("manos");
		u1.setUserPassword("manos");
		u1.setLastVisit(now);
		u1 = userService.create(u1);
    }
}
