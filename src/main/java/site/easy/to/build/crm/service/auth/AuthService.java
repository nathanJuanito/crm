package site.easy.to.build.crm.service.auth;

// Importations adaptées à votre structure de projet
import site.easy.to.build.crm.entity.User; // ou l'entité utilisateur appropriée
import site.easy.to.build.crm.repository.UserRepository; // ou le repository approprié
import site.easy.to.build.crm.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    // Le reste du code reste identique
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

        public String authenticate(String username, String password) {
        List<User> users = userRepository.findByUsername(username);
        
        if (!users.isEmpty()) {
            User user = users.get(0);
            if (passwordEncoder.matches(password, user.getPassword())) {
                return jwtUtil.generateToken(user.getUsername());
            }
        }
        
        throw new RuntimeException("Invalid credentials");
    }

}
