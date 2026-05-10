package vn.fastfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.fastfood.entity.DiaChi;
import vn.fastfood.repository.AddressRepository;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public DiaChi saveDiaChi(DiaChi diaChi) {
        return this.addressRepository.save(diaChi);
    }

    public DiaChi findDiaChi(long maDC) {
        return this.addressRepository.findByMaDC(maDC);
    }

    public boolean hasAddress(long maTK) {
        return this.addressRepository.countByUser_MaTK(maTK) > 0;
    }

    public boolean setDefaultAddress(long maTK, long maDC) {
        DiaChi selectedAddress = this.addressRepository.findByMaDC(maDC);
        if (selectedAddress == null || selectedAddress.getUser() == null
                || selectedAddress.getUser().getMaTK() != maTK) {
            return false;
        }

        DiaChi currentDefault = this.addressRepository.findByUser_MaTKAndDefaultAddressTrue(maTK);
        if (currentDefault != null && currentDefault.getMaDC() != maDC) {
            currentDefault.setDefaultAddress(false);
            this.addressRepository.save(currentDefault);
        }

        selectedAddress.setDefaultAddress(true);
        this.addressRepository.save(selectedAddress);
        return true;
    }

    @Transactional
    public boolean deleteDiaChi(long maTK, long maDC) {
        DiaChi address = this.addressRepository.findByMaDC(maDC);
        if (address == null || address.getUser() == null || address.getUser().getMaTK() != maTK) {
            return false;
        }

        boolean wasDefault = address.isDefaultAddress();
        this.addressRepository.delete(address);

        if (wasDefault) {
            DiaChi nextDefault = this.addressRepository.findFirstByUser_MaTKOrderByMaDCAsc(maTK);
            if (nextDefault != null) {
                nextDefault.setDefaultAddress(true);
                this.addressRepository.save(nextDefault);
            }
        }

        return true;
    }

}
