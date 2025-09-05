package com.example.LT_Web2.services;

import com.example.LT_Web2.models.CompanyModel;
import com.example.LT_Web2.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {
    @Autowired
    private  CompanyRepository companyRepository ;

    public List<CompanyModel> getAllCompanies() {
        return companyRepository.findAll();
    }

    public CompanyModel saveCompany(CompanyModel company) {
        return companyRepository.save(company);
    }

    public CompanyModel getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found: " + id));
    }
}
