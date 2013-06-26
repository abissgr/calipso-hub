package gr.abiss.calipso.controller;

import javax.inject.Inject;
import javax.inject.Named;

import org.resthub.web.controller.RepositoryBasedRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import gr.abiss.calipso.model.Sample;
import gr.abiss.calipso.repository.SampleRepository;

@Controller
@RequestMapping(value = "/api/sample")
public class SampleController extends RepositoryBasedRestController<Sample, Long, SampleRepository> {

    @Inject
    @Named("sampleRepository")
    @Override
    public void setRepository(SampleRepository repository) {
        this.repository = repository;
    }
}
