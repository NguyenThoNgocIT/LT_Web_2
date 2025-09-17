package com.example.LT_Web2.controllers.api;

import com.example.LT_Web2.models.CompanyModel;
import com.example.LT_Web2.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyApiController {

    @Autowired
    private CompanyService companyService;

    @GetMapping
    public List<CompanyModel> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/{id}")
    public CompanyModel getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id);
    }

    @PostMapping
    public CompanyModel createCompany(@RequestBody CompanyModel company) {
        return companyService.saveCompany(company);
    }

    @PutMapping("/{id}")
    public CompanyModel updateCompany(@PathVariable Long id, @RequestBody CompanyModel company) {
        return companyService.updateCompany(id, company);
    }

    @DeleteMapping("/{id}")
    public void deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
    }
}
