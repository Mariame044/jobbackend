package odk.apprenant.jobaventure_backend.controller;



import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Professionnel;
import odk.apprenant.jobaventure_backend.model.Role;
import odk.apprenant.jobaventure_backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Endpoint to create a new admin
    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin createdAdmin = adminService.createAdmin(admin);
        return ResponseEntity.ok(createdAdmin);
    }

    // Endpoint to get an admin by ID
    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        Admin admin = adminService.getAdminById(id);
        return admin != null ? ResponseEntity.ok(admin) : ResponseEntity.notFound().build();
    }

    // Endpoint to get all admins
    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    // Endpoint to delete an admin by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint to add a professional to the authenticated admin
    @PostMapping("/professionnels")
    public ResponseEntity<String> addProfessionnel(@RequestBody Professionnel professionnel) {
        try {
            adminService.addProfessionnelToAdmin(professionnel);
            return ResponseEntity.ok("Professionnel ajouté avec succès!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to add a role type
    @PostMapping("/roles")
    public ResponseEntity<String> addRoleType(@RequestBody Role role) {
        String message = adminService.ajouterRoleType(role);
        return ResponseEntity.ok(message);
    }

    // Endpoint to modify a role type
    @PutMapping("/roles/{id}")
    public ResponseEntity<String> modifyRoleType(@PathVariable Long id, @RequestBody Role roleDetails) {
        String message = adminService.modifierRoleType(id, roleDetails);
        return ResponseEntity.ok(message);
    }

    // Endpoint to delete a role type
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<String> deleteRoleType(@PathVariable Long id) {
        String message = adminService.supprimerRoleType(id);
        return ResponseEntity.ok(message);
    }
}
