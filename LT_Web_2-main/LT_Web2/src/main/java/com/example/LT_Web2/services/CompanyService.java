package com.example.LT_Web2.services;

import com.example.LT_Web2.models.CompanyModel;
import com.example.LT_Web2.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository; // Repository thao tác DB cho Company

    // Lấy danh sách tất cả công ty
    public List<CompanyModel> getAllCompanies() {
        return companyRepository.findAll();
    }

    // Lưu công ty mới hoặc cập nhật công ty
    public CompanyModel saveCompany(CompanyModel company) {
        return companyRepository.save(company);
    }

    // Lấy công ty theo ID
    public CompanyModel getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy công ty với ID: " + id));
    }

    // Xóa công ty theo ID, kiểm tra nếu vẫn còn user liên kết thì báo lỗi
    public void deleteCompanyById(Long id) {
        CompanyModel company = getCompanyById(id);
        if (!company.getUsers().isEmpty()) {
            throw new IllegalStateException("Không thể xóa công ty vì vẫn còn người dùng liên kết.");
        }
        companyRepository.deleteById(id);
    }

    // Cập nhật công ty theo ID
    public CompanyModel updateCompany(Long id, CompanyModel company) {
        CompanyModel existingCompany = getCompanyById(id);
        existingCompany.setCompanyName(company.getCompanyName());
        // Nếu sau này có thêm các field khác như address, email thì cập nhật ở đây
        return companyRepository.save(existingCompany);
    }

    // Wrapper method cho deleteCompanyById
    public void deleteCompany(Long id) {
        deleteCompanyById(id); // gọi lại hàm xóa có sẵn
    }
}
