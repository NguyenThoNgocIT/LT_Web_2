package com.example.LT_Web2.services;

import com.example.LT_Web2.models.CompanyModel;
import com.example.LT_Web2.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public List<CompanyModel> getAllCompanies() {
        return companyRepository.findAll();
    }

    public CompanyModel saveCompany(CompanyModel company) {
        return companyRepository.save(company);
    }

    public CompanyModel getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy công ty với ID: " + id));
    }

    public void deleteCompanyById(Long id) {
        CompanyModel company = getCompanyById(id);
        if (!company.getUsers().isEmpty()) {
            throw new IllegalStateException("Không thể xóa công ty vì vẫn còn người dùng liên kết.");
        }
        companyRepository.deleteById(id);
    }
    // public void deleteCompanyById(Long id) {
    // CompanyModel company = getCompanyById(id);
    // // Cập nhật người dùng để bỏ liên kết
    // company.getUsers().forEach(user -> {
    // user.setCompany(null);
    // UserRepository.save(user);
    // });
    // companyRepository.deleteById(id);
    // }

    // Thêm hàm updateCompany để Controller gọi được
    public CompanyModel updateCompany(Long id, CompanyModel company) {
        CompanyModel existingCompany = getCompanyById(id);
        existingCompany.setCompanyName(company.getCompanyName());
        // nếu sau này bạn thêm field Address, Email thì mới update thêm ở đây
        return companyRepository.save(existingCompany);
    }

    // Thêm hàm deleteCompany để Controller gọi được
    public void deleteCompany(Long id) {
        deleteCompanyById(id); // gọi lại hàm bạn đã có sẵn
    }

}
