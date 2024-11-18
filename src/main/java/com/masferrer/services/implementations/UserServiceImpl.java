package com.masferrer.services.implementations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.masferrer.configs.JwtService;
import com.masferrer.models.dtos.AssignSubjectToTeacherDTO;
import com.masferrer.models.dtos.AuthenticationResponse;
import com.masferrer.models.dtos.EditUserDTO;
import com.masferrer.models.dtos.ForgotPasswordDTO;
import com.masferrer.models.dtos.ForgotPasswordResponseDTO;
import com.masferrer.models.dtos.LoginDTO;
import com.masferrer.models.dtos.RegisterDTO;
import com.masferrer.models.dtos.ShortUserDTO;
import com.masferrer.models.dtos.UserDTO;
import com.masferrer.models.dtos.WhoAmIDTO;
import com.masferrer.models.entities.Role;
import com.masferrer.models.entities.Subject;
import com.masferrer.models.entities.User;
import com.masferrer.models.entities.User_X_Subject;
import com.masferrer.repository.RoleRepository;
import com.masferrer.repository.SubjectRepository;
import com.masferrer.repository.UserRepository;
import com.masferrer.repository.User_X_SubjectRepository;
import com.masferrer.services.UserService;
import com.masferrer.utils.AutoCodeGeneration;
import com.masferrer.utils.EmailUtil;
import com.masferrer.utils.EntityMapper;
import com.masferrer.utils.ExistExceptions;
import com.masferrer.utils.NotFoundException;
import com.masferrer.utils.UserInactiveException;
import com.masferrer.utils.VerificationCode;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
@Service
public class UserServiceImpl implements UserService {

    //inyectando los repositorios
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    User_X_SubjectRepository user_X_SubjectRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    EntityMapper entityMapper;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private AutoCodeGeneration autoCodeGeneration;

    /* Map para poder trabajar con los codigos de verificacion */
    private Map<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> findAll(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable);
    }

    @Override
    public User findById(UUID id) {
        User user = userRepository.findOneById(id);
        if(user == null)
            throw new NotFoundException("User not found");

        return user;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserDTO register(RegisterDTO registerInfo) throws Exception {
        //si el email de un usuario ya existe no se vuelve a registrar
        Boolean doesExist = userRepository.findByEmail(registerInfo.getEmail()).isPresent();
        if(doesExist){
            throw new ExistExceptions("Email already exists");
        }
        
        Role role = roleRepository.findById(registerInfo.getId_role()).orElseThrow( () -> new NotFoundException("Role not found"));
        
        User user = new User(
            registerInfo.getName(),
            registerInfo.getEmail(),
            passwordEncoder.encode(registerInfo.getPassword()),
            role,
            registerInfo.getVerified_email()
        );

        user.setActive(true);
        User response = userRepository.save(user);
        return entityMapper.mapUser(response);
    }

    @Override
    public AuthenticationResponse login(LoginDTO loginInfo) {
        // Buscar usuario por email
        User user = userRepository.findByEmail(loginInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Verificar la contraseña
        if (!passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        //ver si el usuario esta activado
        if (!user.getActive()) {
        throw new UserInactiveException("User is inactive");
    }

        // Generar token JWT
        String token = jwtService.generateToken(user);

        // Crear la respuesta de autenticación
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public List<ShortUserDTO> findAllUsers() {
        List<User> users = userRepository.findAll();
        List<ShortUserDTO> userDTOs = users.stream()
        .map(user -> new ShortUserDTO(user.getId(), user.getName(), user.getEmail(), user.getActive(), user.getVerifiedEmail()))
        .collect(Collectors.toList());
        return userDTOs;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean editUser(EditUserDTO userInfo, UUID id) {
        User userToUpdate = userRepository.findById(id).orElseThrow( () -> new NotFoundException("User not found"));
        
        //ver que el email del profesor no este tomado
        User existEmailUser = userRepository.findByEmail(userInfo.getEmail()).orElse(null);
        if (existEmailUser != null && !existEmailUser.getId().equals(id)) {
            // Si existe un usuario con el mismo correo electrónico y no es el usuario que estamos actualizando,
            // entonces no podemos actualizar el usuario.
            return false;
        }

        // si se proporcionan los datos actualizar sino no y ver que si se deja en vacio no se actualice
        if (userInfo.getName() != null && !userInfo.getName().trim().isEmpty()) {
            userToUpdate.setName(userInfo.getName());
        }
        if (userInfo.getEmail() != null && !userInfo.getEmail().trim().isEmpty()) {
            userToUpdate.setEmail(userInfo.getEmail());
        }
        if (userInfo.getId_role() != null && !userInfo.getId_role().equals(new UUID(0, 0))) {
            Role role = roleRepository.findById(userInfo.getId_role()).orElseThrow( () -> new NotFoundException("Role not found"));
            if(role == null)
                return false;
            userToUpdate.setRole(role);
        }
        if (userInfo.getVerifiedEmail() != null && !userInfo.getVerifiedEmail().trim().isEmpty()) {
            userToUpdate.setVerifiedEmail(userInfo.getVerifiedEmail());
        }
        if (userInfo.getPassword() != null && !userInfo.getPassword().trim().isEmpty()) {
            if (userInfo.getPassword().length() < 8) {
                throw new IllegalArgumentException("Password must have at least 8 characters");
            }
            if (!userInfo.getPassword().matches("^(?=.*[0-9]).*$")) {
                throw new IllegalArgumentException("Password must have at least one number");
            }
            if (!userInfo.getPassword().matches("^(?=.*[a-z]).*$")) {
                throw new IllegalArgumentException("Password must have at least one lowercase letter");
            }
            if (!userInfo.getPassword().matches("^(?=.*[A-Z]).*$")) {
                throw new IllegalArgumentException("Password must have at least one uppercase letter");
            }
            if (!userInfo.getPassword().matches("^(?=.*[@#$%^&+=!{}.,<>\\-+*;:'/\\?¡¿_]).*$")) {
                throw new IllegalArgumentException("Password must have at least one special character");
            }
            userToUpdate.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        }
        userRepository.save(userToUpdate);
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean deleteUser(UUID id) {
        User userToDelete = userRepository.findById(id).orElse(null);

        if(userToDelete == null){
            return false;
        }
        userRepository.delete(userToDelete);
        return true;
    }

    @Override
    public List<UserDTO> showUsersAdmin() {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        List<User> users = userRepository.findAll(sort);
        List<UserDTO> userAdminDTOs = users.stream()
        .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getActive(), user.getVerifiedEmail()))
        .collect(Collectors.toList());
        
        return userAdminDTOs;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void changePassword(ForgotPasswordDTO forgotPasswordDTO) throws Exception {
        
        String email = forgotPasswordDTO.getEmail();
        String newPassword = forgotPasswordDTO.getNewPassword();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new Exception("User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<User> findUsersByRoleId(UUID roleId) {
        if(!roleRepository.existsById(roleId)){
            throw new NotFoundException("Role not found");
        }
        return userRepository.findUsersByRoleId(roleId);
    }

    @Override
    public Page<User> findUsersByRoleId(UUID roleId,int page, int size) {
        if(!roleRepository.existsById(roleId)){
            throw new NotFoundException("Role not found");
        }
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findUsersByRoleId(roleId, pageable);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public WhoAmIDTO whoAmI() {
        // Get the User from the JWT token
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Create a WhoAmIDTO with the user's details
        WhoAmIDTO whoAmIDTO = new WhoAmIDTO(user.getName(), user.getEmail(), user.getRole(), user.getVerifiedEmail());
        
        return whoAmIDTO;
    }

    @Override
    public void assignSubjectToTeacher(AssignSubjectToTeacherDTO info) {
        User user = userRepository.findOneById(info.getId_user());
        Subject subject = subjectRepository.findOneById(info.getId_subject());

        if(user!= null && subject != null){
            //verificando de que si el usuario y materia no es null, ver si no ha sido asginado anteriormente
            boolean alreadyAssigned = user_X_SubjectRepository.existsByUserIdAndSubjectId(info.getId_user(), info.getId_subject());
            if(alreadyAssigned){
                throw new IllegalArgumentException("User already assigned to this subject");
            }
            //si el usuario no ha sido asignado se crea
            User_X_Subject user_X_Subject = new User_X_Subject(user, subject);
            user_X_SubjectRepository.save(user_X_Subject);
        }
        else{
            throw new IllegalArgumentException("Invalid user or subject id");
        }
    }

    @Override
    public List<User> getUsersBySubjectId(UUID subjectId) throws Exception {
        if(!subjectRepository.existsById(subjectId)){
            throw new NotFoundException("Subject not found");
        }
        return user_X_SubjectRepository.findUsersBySubjectId(subjectId);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean toggleActiveStatus(UUID id) throws Exception {
        User user = userRepository.findById(id).orElse(null);
        if(user == null){
            return false;
        }
        
        user.setActive(!user.getActive());
        userRepository.save(user);
        return true;
    }

    @Override
    public ForgotPasswordResponseDTO forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
            () -> new RuntimeException("Usuario con " + email + " no encontrado")
        );
        //generamos el codigo de verificacion random
        String verificationCode = autoCodeGeneration.generateVerificationCode();
        //se dara un plazo de 10 minutos para que el usuario pueda verificar su cuenta
        VerificationCode verificationCodeWithExpiry = new VerificationCode(verificationCode, LocalDateTime.now().plusMinutes(10));
        verificationCodes.put(email, verificationCodeWithExpiry);//guardamos el codigo en el map
        try {
            emailUtil.sendVerificationCodeEmail(user.getVerifiedEmail(), verificationCode, user.getName(), user.getEmail());//mandamos el correo con el codigo
        } catch (MessagingException e) {
            throw new RuntimeException("Error al mandar el correo, intentelo de nuevo");
        }
        return new ForgotPasswordResponseDTO(user.getVerifiedEmail());
    }
    //metodo para poder verificar codigos
    @Override
    public String verifyCode(String email, String code){
        VerificationCode storedCode = verificationCodes.get(email);
    
        if (storedCode == null || storedCode.isExpired()) {
            verificationCodes.remove(email);  // eliminamos el codigo si ha expirado o no existe
            return null;  // retornamos null si el codido es invalido o expirado
        }
        
        //vemos si el codigo recibido es igual al codigo almacenado
        if (code.equals(storedCode.getCode())) {
            return email;  // retornamos el correo si el codigo es valido
        }
    
        return null;  // retornamos null si el codigo no coincide
    }

    @Override
    public Boolean setPassword(String email, String newPassword) {
        //obtenemos el codigo para ver si ya se uso o esta expirado
        VerificationCode storedCode = verificationCodes.get(email);

        if(storedCode == null || storedCode.isExpired()){
            return null;
        }
        //si el codigo es correcto a la hora de usar el codigo se eliminara para que se use solo una vez
        verificationCodes.remove(email);

        User user = userRepository.findByEmail(email).orElseThrow(
            () -> new RuntimeException("Usuario con " + email + " no encontrado")
        );
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public List<User> findByRoleIdAndActive(UUID roleId) {
        if(!roleRepository.existsById(roleId)){
            throw new NotFoundException("Role not found");
        }
        return userRepository.findByRoleIdAndActive(roleId, true);
    }
}
