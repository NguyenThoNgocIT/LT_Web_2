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
        // [
        // { "id": 1, "companyName": "OpenAI" },
        // { "id": 2, "companyName": "Google" }
        // ]
    }

    //  lấy thông tin chi tiết 1 công ty theo ID
    @GetMapping("/{id}")
    public CompanyModel getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id);
    }

    // Tạo công ty mới.
    @PostMapping
    public CompanyModel createCompany(@RequestBody CompanyModel company) {
        return companyService.saveCompany(company);
    }

    // Cập nhật công ty có sẵn (dựa vào ID).
    @PutMapping("/{id}")
    public CompanyModel updateCompany(@PathVariable Long id, @RequestBody CompanyModel company) {
        return companyService.updateCompany(id, company);
    }

    // Xóa công ty theo ID.
    @DeleteMapping("/{id}")
    public void deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
    }
}
