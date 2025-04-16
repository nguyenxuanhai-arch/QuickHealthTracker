package com.vaccine.controller.admin;

import com.vaccine.dto.VaccineDTO;
import com.vaccine.model.Vaccine;
import com.vaccine.model.VaccineService;
import com.vaccine.repository.VaccineServiceRepository;
import com.vaccine.service.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVaccineController {

    @Autowired
    private VaccineService vaccineService;
    
    @Autowired
    private VaccineServiceRepository vaccineServiceRepository;

    @GetMapping("/vaccines")
    public String showVaccineManagement(Model model) {
        List<Vaccine> vaccines = vaccineService.findAll();
        model.addAttribute("vaccines", vaccines);
        return "admin/vaccines";
    }

    @GetMapping("/vaccines/add")
    public String showAddVaccineForm(Model model) {
        model.addAttribute("vaccine", new Vaccine());
        return "admin/vaccine-add";
    }

    @PostMapping("/vaccines/add")
    public String addVaccine(@ModelAttribute Vaccine vaccine) {
        vaccineService.createVaccine(vaccine);
        return "redirect:/admin/vaccines?added";
    }

    @GetMapping("/vaccines/{id}/edit")
    public String showEditVaccineForm(@PathVariable Long id, Model model) {
        Optional<Vaccine> optionalVaccine = vaccineService.findById(id);
        
        if (optionalVaccine.isPresent()) {
            model.addAttribute("vaccine", optionalVaccine.get());
            return "admin/vaccine-edit";
        } else {
            return "redirect:/admin/vaccines?error=notfound";
        }
    }

    @PostMapping("/vaccines/{id}/update")
    public String updateVaccine(@PathVariable Long id, @ModelAttribute Vaccine vaccine) {
        Optional<Vaccine> existingVaccine = vaccineService.findById(id);
        
        if (existingVaccine.isPresent()) {
            vaccine.setId(id);
            vaccineService.updateVaccine(vaccine);
            return "redirect:/admin/vaccines?updated";
        } else {
            return "redirect:/admin/vaccines?error=notfound";
        }
    }

    @PostMapping("/vaccines/{id}/delete")
    public String deleteVaccine(@PathVariable Long id) {
        try {
            vaccineService.deleteVaccine(id);
            return "redirect:/admin/vaccines?deleted";
        } catch (Exception e) {
            return "redirect:/admin/vaccines?error=cannotdelete";
        }
    }

    @GetMapping("/vaccine-services")
    public String showVaccineServices(Model model) {
        List<com.vaccine.model.VaccineService> services = vaccineServiceRepository.findAll();
        model.addAttribute("services", services);
        return "admin/vaccine-services";
    }

    @GetMapping("/vaccine-services/add")
    public String showAddServiceForm(Model model) {
        List<Vaccine> vaccines = vaccineService.findAll();
        model.addAttribute("service", new com.vaccine.model.VaccineService());
        model.addAttribute("vaccines", vaccines);
        model.addAttribute("serviceTypes", com.vaccine.model.VaccineService.ServiceType.values());
        return "admin/vaccine-service-add";
    }

    @PostMapping("/vaccine-services/add")
    public String addVaccineService(@ModelAttribute com.vaccine.model.VaccineService service, 
                                   @RequestParam(required = false) List<Long> vaccineIds) {
        if (vaccineIds != null && !vaccineIds.isEmpty()) {
            Set<Vaccine> vaccines = service.getVaccines();
            
            for (Long vaccineId : vaccineIds) {
                vaccineService.findById(vaccineId).ifPresent(vaccines::add);
            }
        }
        
        vaccineServiceRepository.save(service);
        return "redirect:/admin/vaccine-services?added";
    }

    @GetMapping("/vaccine-services/{id}/edit")
    public String showEditServiceForm(@PathVariable Long id, Model model) {
        Optional<com.vaccine.model.VaccineService> optionalService = vaccineServiceRepository.findById(id);
        
        if (optionalService.isPresent()) {
            List<Vaccine> allVaccines = vaccineService.findAll();
            
            model.addAttribute("service", optionalService.get());
            model.addAttribute("vaccines", allVaccines);
            model.addAttribute("serviceTypes", com.vaccine.model.VaccineService.ServiceType.values());
            return "admin/vaccine-service-edit";
        } else {
            return "redirect:/admin/vaccine-services?error=notfound";
        }
    }

    @PostMapping("/vaccine-services/{id}/update")
    public String updateVaccineService(@PathVariable Long id, 
                                      @ModelAttribute com.vaccine.model.VaccineService service,
                                      @RequestParam(required = false) List<Long> vaccineIds) {
        Optional<com.vaccine.model.VaccineService> optionalService = vaccineServiceRepository.findById(id);
        
        if (optionalService.isPresent()) {
            com.vaccine.model.VaccineService existingService = optionalService.get();
            existingService.setName(service.getName());
            existingService.setDescription(service.getDescription());
            existingService.setServiceType(service.getServiceType());
            existingService.setPrice(service.getPrice());
            existingService.setDiscountPercentage(service.getDiscountPercentage());
            
            // Update vaccines
            existingService.getVaccines().clear();
            if (vaccineIds != null && !vaccineIds.isEmpty()) {
                for (Long vaccineId : vaccineIds) {
                    vaccineService.findById(vaccineId).ifPresent(v -> existingService.getVaccines().add(v));
                }
            }
            
            vaccineServiceRepository.save(existingService);
            return "redirect:/admin/vaccine-services?updated";
        } else {
            return "redirect:/admin/vaccine-services?error=notfound";
        }
    }

    @PostMapping("/vaccine-services/{id}/delete")
    public String deleteVaccineService(@PathVariable Long id) {
        try {
            vaccineServiceRepository.deleteById(id);
            return "redirect:/admin/vaccine-services?deleted";
        } catch (Exception e) {
            return "redirect:/admin/vaccine-services?error=cannotdelete";
        }
    }

    // REST API endpoints
    @GetMapping("/api/vaccines")
    @ResponseBody
    public List<Vaccine> getAllVaccinesApi() {
        return vaccineService.findAll();
    }

    @PostMapping("/api/vaccines")
    @ResponseBody
    public ResponseEntity<?> createVaccineApi(@RequestBody VaccineDTO vaccineDTO) {
        Vaccine vaccine = new Vaccine();
        vaccine.setName(vaccineDTO.getName());
        vaccine.setDescription(vaccineDTO.getDescription());
        vaccine.setManufacturer(vaccineDTO.getManufacturer());
        vaccine.setRecommendedAgeMonthsStart(vaccineDTO.getRecommendedAgeMonthsStart());
        vaccine.setRecommendedAgeMonthsEnd(vaccineDTO.getRecommendedAgeMonthsEnd());
        vaccine.setIsRequired(vaccineDTO.getIsRequired());
        vaccine.setPreventedDiseases(vaccineDTO.getPreventedDiseases());
        vaccine.setSideEffects(vaccineDTO.getSideEffects());
        vaccine.setPrice(vaccineDTO.getPrice());
        
        Vaccine createdVaccine = vaccineService.createVaccine(vaccine);
        return ResponseEntity.ok(createdVaccine);
    }

    @PutMapping("/api/vaccines/{id}")
    @ResponseBody
    public ResponseEntity<?> updateVaccineApi(@PathVariable Long id, @RequestBody VaccineDTO vaccineDTO) {
        Optional<Vaccine> optionalVaccine = vaccineService.findById(id);
        
        if (optionalVaccine.isPresent()) {
            Vaccine vaccine = optionalVaccine.get();
            vaccine.setName(vaccineDTO.getName());
            vaccine.setDescription(vaccineDTO.getDescription());
            vaccine.setManufacturer(vaccineDTO.getManufacturer());
            vaccine.setRecommendedAgeMonthsStart(vaccineDTO.getRecommendedAgeMonthsStart());
            vaccine.setRecommendedAgeMonthsEnd(vaccineDTO.getRecommendedAgeMonthsEnd());
            vaccine.setIsRequired(vaccineDTO.getIsRequired());
            vaccine.setPreventedDiseases(vaccineDTO.getPreventedDiseases());
            vaccine.setSideEffects(vaccineDTO.getSideEffects());
            vaccine.setPrice(vaccineDTO.getPrice());
            
            Vaccine updatedVaccine = vaccineService.updateVaccine(vaccine);
            return ResponseEntity.ok(updatedVaccine);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/vaccines/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteVaccineApi(@PathVariable Long id) {
        try {
            vaccineService.deleteVaccine(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot delete vaccine. It may be in use.");
        }
    }

    @GetMapping("/api/vaccine-services")
    @ResponseBody
    public List<com.vaccine.model.VaccineService> getAllVaccineServicesApi() {
        return vaccineServiceRepository.findAll();
    }

    @GetMapping("/api/vaccine-services/{id}")
    @ResponseBody
    public ResponseEntity<?> getVaccineServiceApi(@PathVariable Long id) {
        Optional<com.vaccine.model.VaccineService> service = vaccineServiceRepository.findById(id);
        return service.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/vaccine-services")
    @ResponseBody
    public ResponseEntity<?> createVaccineServiceApi(@RequestBody com.vaccine.model.VaccineService service) {
        com.vaccine.model.VaccineService savedService = vaccineServiceRepository.save(service);
        return ResponseEntity.ok(savedService);
    }

    @PutMapping("/api/vaccine-services/{id}")
    @ResponseBody
    public ResponseEntity<?> updateVaccineServiceApi(@PathVariable Long id, 
                                                   @RequestBody com.vaccine.model.VaccineService service) {
        Optional<com.vaccine.model.VaccineService> optionalService = vaccineServiceRepository.findById(id);
        
        if (optionalService.isPresent()) {
            service.setId(id);
            com.vaccine.model.VaccineService updatedService = vaccineServiceRepository.save(service);
            return ResponseEntity.ok(updatedService);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/vaccine-services/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteVaccineServiceApi(@PathVariable Long id) {
        try {
            vaccineServiceRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot delete service. It may be in use.");
        }
    }
}
