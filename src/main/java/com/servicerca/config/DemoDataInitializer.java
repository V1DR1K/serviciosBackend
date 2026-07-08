package com.servicerca.config;

import com.servicerca.model.*;
import com.servicerca.repository.*;
import com.servicerca.service.CryptoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Component
@ConditionalOnProperty(name = "app.demo-data-enabled", havingValue = "true")
public class DemoDataInitializer implements CommandLineRunner {

    private final UserRepository users;
    private final TradeRepository trades;
    private final ServiceRequestRepository requests;
    private final ServiceApplicationRepository applications;
    private final RatingRepository ratings;
    private final ReportRepository reports;
    private final MediaFileRepository mediaFiles;
    private final PasswordEncoder encoder;
    private final CryptoService crypto;

    public DemoDataInitializer(UserRepository u, TradeRepository t, ServiceRequestRepository r,
                               ServiceApplicationRepository a, RatingRepository ra, ReportRepository re,
                               MediaFileRepository m, PasswordEncoder e, CryptoService c) {
        users = u; trades = t; requests = r; applications = a;
        ratings = ra; reports = re; mediaFiles = m; encoder = e; crypto = c;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (users.findByEmailIgnoreCaseAndDeletedAtIsNull("usuario1@gmail.com").isPresent()) return;

        var allTrades = trades.findAll();
        var plumbing = findByCode(allTrades, "PLUMBING");
        var electricity = findByCode(allTrades, "ELECTRICITY");
        var cleaning = findByCode(allTrades, "CLEANING");
        var gardening = findByCode(allTrades, "GARDENING");
        var gas = findByCode(allTrades, "GAS");
        var carpentry = findByCode(allTrades, "CARPENTRY");
        var painting = findByCode(allTrades, "PAINTING");
        var roofing = findByCode(allTrades, "ROOFING");

        Instant now = Instant.now();

        // ============ USERS ============
        var u1 = saveUser(1, "Lucía Martínez", false, true,
                "32999111", "Rosario", -32.9468, -60.6393, now.minus(Duration.ofDays(88)));
        var u2 = saveUser(2, "Carlos Pérez", false, true,
                "33000111", "Funes", -32.9167, -60.8167, now.minus(Duration.ofDays(87)));
        var u3 = saveUser(3, "Valentina Díaz", false, true,
                "33001111", "Fisherton", -32.9100, -60.7000, now.minus(Duration.ofDays(86)));
        var u4 = saveUser(4, "Martín López", false, true,
                "33002111", "Roldán", -32.9000, -60.9000, now.minus(Duration.ofDays(85)));
        var u5 = saveUser(5, "Camila Fernández", false, true,
                "33003111", "Pérez", -33.0000, -60.7667, now.minus(Duration.ofDays(84)));
        var u6 = saveUser(6, "Roberto Sánchez", true, true,
                "30111222", "Granadero Baigorria", -32.8500, -60.7000, now.minus(Duration.ofDays(90)));
        var u7 = saveUser(7, "Andrea Ruiz", true, true,
                "30222333", "San Lorenzo", -32.7500, -60.7333, now.minus(Duration.ofDays(89)));
        var u8 = saveUser(8, "Gustavo Torres", true, true,
                "30333444", "Villa Gdor. Gálvez", -33.0333, -60.6333, now.minus(Duration.ofDays(88)));
        var u9 = saveUser(9, "Marina Castillo", true, true,
                "30444555", "Rosario Sur", -32.9700, -60.6300, now.minus(Duration.ofDays(87)));
        var u10 = saveUser(10, "Diego Acosta", true, true,
                "30555666", "Rosario Norte", -32.9300, -60.6500, now.minus(Duration.ofDays(86)));

        var allUsers = List.of(u1, u2, u3, u4, u5, u6, u7, u8, u9, u10);

        // Assign trades to professionals
        u6.getTrades().addAll(List.of(plumbing, gas));
        u7.getTrades().addAll(List.of(electricity, painting));
        u8.getTrades().addAll(List.of(gardening, roofing, cleaning));
        u9.getTrades().addAll(List.of(cleaning, painting));
        u10.getTrades().addAll(List.of(carpentry, plumbing));
        users.saveAll(allUsers);

        // Update professional ratings after initial save
        u6.setRating(new BigDecimal("4.8")); u6.setRatingCount(32);
        u7.setRating(new BigDecimal("4.9")); u7.setRatingCount(28);
        u8.setRating(new BigDecimal("4.5")); u8.setRatingCount(18);
        u9.setRating(new BigDecimal("4.7")); u9.setRatingCount(22);
        u10.setRating(new BigDecimal("4.3")); u10.setRatingCount(15);
        users.saveAll(List.of(u6, u7, u8, u9, u10));

        // ============ SERVICE REQUESTS & FLOW ============

        // --- MONTH 1: 90-60 days ago (fully completed) ---

        // Request 1: u1 -> plumbing (urgent), completed
        var req1 = saveRequest(u1, plumbing, "Cambio de termotanque",
                "Se rompió el termotanque eléctrico de 80 litros. Necesito urgente que lo cambien, tengo el repuesto nuevo comprado.",
                "Rosario", -32.9468, -60.6393, Urgency.URGENT, new BigDecimal("15000"),
                RequestStatus.COMPLETED, now.minus(Duration.ofDays(85)));
        var app1a = saveApplication(req1, u6, "Soy gasista matriculado. Puedo ir hoy mismo.", now.minus(Duration.ofDays(84)));
        var app1b = saveApplication(req1, u10, "Tengo experiencia con termotanques eléctricos.", now.minus(Duration.ofDays(84)));
        app1a.setStatus(ApplicationStatus.SELECTED);
        applications.save(app1a);
        req1.setSelectedApplication(app1a);
        req1.setClientCompleted(true);
        req1.setProfessionalCompleted(true);
        requests.save(req1);
        saveRating(req1, u1, u6, (short) 5, "Muy rápido y profesional. Lo recomiendo.", now.minus(Duration.ofDays(80)));
        saveRating(req1, u6, u1, (short) 5, "Buena cliente, todo claro.", now.minus(Duration.ofDays(80)));
        users.saveAll(List.of(updateUserRating(u6, 5), updateUserRating(u1, 5)));

        // Request 2: u2 -> electricity, completed
        var req2 = saveRequest(u2, electricity, "Instalación de aire acondicionado",
                "Necesito que instalen un aire acondicionado split de 3000 frigorías en mi living. Ya tengo el equipo.",
                "Funes", -32.9167, -60.8167, Urgency.NORMAL, new BigDecimal("25000"),
                RequestStatus.COMPLETED, now.minus(Duration.ofDays(82)));
        var app2a = saveApplication(req2, u7, "Soy electricista matriculado. Instalación incluida.", now.minus(Duration.ofDays(81)));
        var app2b = saveApplication(req2, u6, "Puedo hacer la instalación eléctrica.", now.minus(Duration.ofDays(81)));
        app2a.setStatus(ApplicationStatus.SELECTED);
        applications.save(app2a);
        req2.setSelectedApplication(app2a);
        req2.setClientCompleted(true);
        req2.setProfessionalCompleted(true);
        requests.save(req2);
        saveRating(req2, u2, u7, (short) 5, "Excelente trabajo, quedó impecable.", now.minus(Duration.ofDays(77)));
        saveRating(req2, u7, u2, (short) 4, "Todo bien, puntual.", now.minus(Duration.ofDays(77)));
        users.saveAll(List.of(updateUserRating(u7, 5), updateUserRating(u2, 4)));

        // Request 3: u4 -> gardening, completed
        var req3 = saveRequest(u4, gardening, "Mantenimiento de jardín mensual",
                "Busco un jardinero para mantenimiento semanal de jardín de 200m2. Incluye cortar pasto, podar arbustos y mantener las plantas.",
                "Roldán", -32.9000, -60.9000, Urgency.NORMAL, new BigDecimal("8000"),
                RequestStatus.COMPLETED, now.minus(Duration.ofDays(78)));
        var app3a = saveApplication(req3, u8, "Soy jardinero con 10 años de experiencia.", now.minus(Duration.ofDays(77)));
        app3a.setStatus(ApplicationStatus.SELECTED);
        applications.save(app3a);
        req3.setSelectedApplication(app3a);
        req3.setClientCompleted(true);
        req3.setProfessionalCompleted(true);
        requests.save(req3);
        saveRating(req3, u4, u8, (short) 5, "Muy dedicado, el jardín quedó hermoso.", now.minus(Duration.ofDays(73)));
        saveRating(req3, u8, u4, (short) 5, "Cliente excelente, paga a tiempo.", now.minus(Duration.ofDays(73)));
        users.saveAll(List.of(updateUserRating(u8, 5), updateUserRating(u4, 5)));

        // --- MONTH 2: 60-30 days ago (mostly completed) ---

        // Request 4: u3 -> gas, completed
        var req4 = saveRequest(u3, gas, "Revisión y certificación de instalación de gas",
                "Necesito un gasista matriculado para revisar la instalación de gas de mi departamento y emitir el certificado anual.",
                "Fisherton", -32.9100, -60.7000, Urgency.NORMAL, new BigDecimal("12000"),
                RequestStatus.COMPLETED, now.minus(Duration.ofDays(55)));
        var app4a = saveApplication(req4, u6, "Gasista matriculado N° 1234. Hago certificaciones.", now.minus(Duration.ofDays(54)));
        var app4b = saveApplication(req4, u10, "Tengo matrícula vigente, puedo ir esta semana.", now.minus(Duration.ofDays(54)));
        app4a.setStatus(ApplicationStatus.SELECTED);
        applications.save(app4a);
        req4.setSelectedApplication(app4a);
        req4.setClientCompleted(true);
        req4.setProfessionalCompleted(true);
        requests.save(req4);
        saveRating(req4, u3, u6, (short) 5, "Muy profesional, dejó todo en regla.", now.minus(Duration.ofDays(50)));
        saveRating(req4, u6, u3, (short) 5, "Todo perfecto.", now.minus(Duration.ofDays(50)));
        users.saveAll(List.of(updateUserRating(u6, 5), updateUserRating(u3, 5)));

        // Request 5: u5 -> painting, completed
        var req5 = saveRequest(u5, painting, "Pintar departamento 2 ambientes",
                "Necesito pintar todo el departamento (50m2). Paredes lisas, colores claros. Incluye mano de obra y materiales.",
                "Rosario Norte", -32.9300, -60.6500, Urgency.NORMAL, new BigDecimal("35000"),
                RequestStatus.COMPLETED, now.minus(Duration.ofDays(50)));
        var app5a = saveApplication(req5, u9, "Pinto departamentos, materiales incluidos.", now.minus(Duration.ofDays(49)));
        var app5b = saveApplication(req5, u7, "Hago pintura de interiores, presupuesto sin cargo.", now.minus(Duration.ofDays(49)));
        app5a.setStatus(ApplicationStatus.SELECTED);
        applications.save(app5a);
        req5.setSelectedApplication(app5a);
        req5.setClientCompleted(true);
        req5.setProfessionalCompleted(true);
        requests.save(req5);
        saveRating(req5, u5, u9, (short) 4, "Buen trabajo, pero tardó un par de días más de lo acordado.", now.minus(Duration.ofDays(45)));
        saveRating(req5, u9, u5, (short) 5, "Buena cliente, recomiendo.", now.minus(Duration.ofDays(45)));
        users.saveAll(List.of(updateUserRating(u9, 4), updateUserRating(u5, 5)));

        // Request 6: u1 -> cleaning, completed
        var req6 = saveRequest(u1, cleaning, "Limpieza profunda de casa",
                "Busco servicio de limpieza profunda para casa de 3 dormitorios, 2 baños. Incluye cocina, ventanas y terraza.",
                "Rosario", -32.9468, -60.6393, Urgency.NORMAL, new BigDecimal("15000"),
                RequestStatus.COMPLETED, now.minus(Duration.ofDays(45)));
        var app6a = saveApplication(req6, u9, "Hacemos limpieza profunda con equipo profesional.", now.minus(Duration.ofDays(44)));
        var app6b = saveApplication(req6, u8, "Servicio de limpieza general, productos incluidos.", now.minus(Duration.ofDays(44)));
        app6a.setStatus(ApplicationStatus.SELECTED);
        applications.save(app6a);
        req6.setSelectedApplication(app6a);
        req6.setClientCompleted(true);
        req6.setProfessionalCompleted(true);
        requests.save(req6);
        saveRating(req6, u1, u9, (short) 5, "Quedó impecable, volvería a contratar.", now.minus(Duration.ofDays(40)));
        saveRating(req6, u9, u1, (short) 5, "Muy ordenada la casa, fácil de limpiar.", now.minus(Duration.ofDays(40)));
        users.saveAll(List.of(updateUserRating(u9, 5), updateUserRating(u1, 5)));

        // Request 7: u2 -> carpentry, completed
        var req7 = saveRequest(u2, carpentry, "Hacer placard a medida",
                "Necesito un carpintero para hacer un placard a medida de 2.40m x 1.80m en el dormitorio. Con puertas corredizas.",
                "Funes", -32.9167, -60.8167, Urgency.NORMAL, new BigDecimal("45000"),
                RequestStatus.COMPLETED, now.minus(Duration.ofDays(40)));
        var app7a = saveApplication(req7, u10, "Carpintero especializado en placard a medida.", now.minus(Duration.ofDays(39)));
        app7a.setStatus(ApplicationStatus.SELECTED);
        applications.save(app7a);
        req7.setSelectedApplication(app7a);
        req7.setClientCompleted(true);
        req7.setProfessionalCompleted(true);
        requests.save(req7);
        saveRating(req7, u2, u10, (short) 4, "Buen trabajo, pero el plazo se extendió una semana.", now.minus(Duration.ofDays(33)));
        saveRating(req7, u10, u2, (short) 5, "Cliente paciente, todo en orden.", now.minus(Duration.ofDays(33)));
        users.saveAll(List.of(updateUserRating(u10, 4), updateUserRating(u2, 5)));

        // Request 8: u4 -> roofing, completed
        var req8 = saveRequest(u4, roofing, "Reparación de filtraciones en techo",
                "Tengo filtraciones en el techo del living cuando llueve. Necesito urgente que lo revisen y reparen antes de la próxima lluvia.",
                "Granadero Baigorria", -32.8500, -60.7000, Urgency.URGENT, new BigDecimal("20000"),
                RequestStatus.COMPLETED, now.minus(Duration.ofDays(35)));
        var app8a = saveApplication(req8, u8, "Techista con experiencia en filtraciones. Garantía 1 año.", now.minus(Duration.ofDays(34)));
        app8a.setStatus(ApplicationStatus.SELECTED);
        applications.save(app8a);
        req8.setSelectedApplication(app8a);
        req8.setClientCompleted(true);
        req8.setProfessionalCompleted(true);
        requests.save(req8);
        saveRating(req8, u4, u8, (short) 5, "Solucionó el problema rápido, no volvió a filtrar.", now.minus(Duration.ofDays(30)));
        saveRating(req8, u8, u4, (short) 4, "Trabajo bien, acceso complicado.", now.minus(Duration.ofDays(30)));
        users.saveAll(List.of(updateUserRating(u8, 5), updateUserRating(u4, 4)));

        // --- MONTH 3: 30-0 days ago (mixed states) ---

        // Request 9: u3 -> plumbing, completed (just now)
        var req9 = saveRequest(u3, plumbing, "Destapar cañería de cocina",
                "La cañería de la cocina está totalmente tapada, no drena el agua. Ya probé con destapador químico pero no funcionó.",
                "Fisherton", -32.9100, -60.7000, Urgency.URGENT, new BigDecimal("8000"),
                RequestStatus.COMPLETED, now.minus(Duration.ofDays(20)));
        var app9a = saveApplication(req9, u10, "Destapaciones con máquina profesional. Resultado inmediato.", now.minus(Duration.ofDays(19)));
        var app9b = saveApplication(req9, u6, "Plomero experto en destapaciones.", now.minus(Duration.ofDays(19)));
        app9a.setStatus(ApplicationStatus.SELECTED);
        applications.save(app9a);
        req9.setSelectedApplication(app9a);
        req9.setClientCompleted(true);
        req9.setProfessionalCompleted(true);
        requests.save(req9);
        saveRating(req9, u3, u10, (short) 4, "Solucionó el problema, pero un poco caro.", now.minus(Duration.ofDays(17)));
        saveRating(req9, u10, u3, (short) 5, "Todo bien.", now.minus(Duration.ofDays(17)));
        users.saveAll(List.of(updateUserRating(u10, 4), updateUserRating(u3, 5)));

        // Request 10: u5 -> electricity (in progress)
        var req10 = saveRequest(u5, electricity, "Recableado parcial de casa",
                "Necesito cambiar la instalación eléctrica vieja del living y cocina. Los cables son muy viejos y saltan los tapones.",
                "Pérez", -33.0000, -60.7667, Urgency.NORMAL, new BigDecimal("30000"),
                RequestStatus.IN_PROGRESS, now.minus(Duration.ofDays(10)));
        var app10a = saveApplication(req10, u7, "Electricista matriculado. Renuevo instalaciones completas.", now.minus(Duration.ofDays(9)));
        app10a.setStatus(ApplicationStatus.SELECTED);
        applications.save(app10a);
        req10.setSelectedApplication(app10a);
        requests.save(req10);

        // Request 11: u1 -> gardening (in progress)
        var req11 = saveRequest(u1, gardening, "Diseño de jardín con plantas autóctonas",
                "Quiero rediseñar el jardín frontal con plantas autóctonas de bajo mantenimiento. Incluye preparación del suelo y riego.",
                "Rosario", -32.9468, -60.6393, Urgency.NORMAL, new BigDecimal("18000"),
                RequestStatus.IN_PROGRESS, now.minus(Duration.ofDays(8)));
        var app11a = saveApplication(req11, u8, "Diseño de jardines autóctonos, riego incluido.", now.minus(Duration.ofDays(7)));
        var app11b = saveApplication(req11, u9, "Puedo hacer el diseño y la plantación.", now.minus(Duration.ofDays(7)));
        app11a.setStatus(ApplicationStatus.SELECTED);
        applications.save(app11a);
        req11.setSelectedApplication(app11a);
        requests.save(req11);

        // Request 12: u2 -> painting (assigned)
        var req12 = saveRequest(u2, painting, "Pintar fachada de casa",
                "Necesito pintar la fachada completa de la casa. Son aproximadamente 80m2 de pared exterior. Presupuesto con materiales.",
                "San Lorenzo", -32.7500, -60.7333, Urgency.NORMAL, new BigDecimal("28000"),
                RequestStatus.ASSIGNED, now.minus(Duration.ofDays(5)));
        var app12a = saveApplication(req12, u7, "Pinto fachadas, incluye hidrolavado previo.", now.minus(Duration.ofDays(4)));
        var app12b = saveApplication(req12, u9, "Pintora profesional, presupuesto con materiales incluidos.", now.minus(Duration.ofDays(4)));
        app12b.setStatus(ApplicationStatus.SELECTED);
        applications.save(app12b);
        req12.setSelectedApplication(app12b);
        requests.save(req12);

        // Request 13: u3 -> cleaning (published, no applicants yet)
        saveRequest(u3, cleaning, "Limpieza de oficina semanal",
                "Busco servicio de limpieza para oficina de 4 ambientes (80m2). Una vez por semana, incluye pisos, baño y cocina.",
                "Rosario Sur", -32.9700, -60.6300, Urgency.NORMAL, new BigDecimal("6000"),
                RequestStatus.PUBLISHED, now.minus(Duration.ofDays(2)));

        // Request 14: u4 -> carpentry (cancelled)
        var req14 = saveRequest(u4, carpentry, "Reparar puertas de placard",
                "Tengo 3 puertas de placard que se descarrilaron. Necesito que las reparen o cambien los rieles.",
                "Roldán", -32.9000, -60.9000, Urgency.NORMAL, new BigDecimal("5000"),
                RequestStatus.CANCELLED, now.minus(Duration.ofDays(12)));
        var app14a = saveApplication(req14, u10, "Reparo rieles de placard, tengo los repuestos.", now.minus(Duration.ofDays(11)));
        app14a.setStatus(ApplicationStatus.DECLINED);
        applications.save(app14a);

        // Request 15: u5 -> gas (published)
        saveRequest(u5, gas, "Instalación de cocina industrial",
                "Necesito instalar una cocina industrial en un pequeño comedor. Requiere conexión de gas y certificación.",
                "Villa Gdor. Gálvez", -33.0333, -60.6333, Urgency.NORMAL, new BigDecimal("22000"),
                RequestStatus.PUBLISHED, now.minus(Duration.ofDays(3)));

        // Request 16: u1 -> electricity (published, new)
        saveRequest(u1, electricity, "Instalar sensores de movimiento en pasillo",
                "Quiero instalar 3 sensores de movimiento en el pasillo de mi casa para que las luces se enciendan automáticamente.",
                "Rosario", -32.9468, -60.6393, Urgency.NORMAL, new BigDecimal("7000"),
                RequestStatus.PUBLISHED, now.minus(Duration.ofDays(1)));

        // ============ REPORTS ============
        saveReport(u5, u10, req9, "TRABAJO_INCOMPLETO",
                "El profesional cobró por adelantado y dejó la cañería mal reparada, volvió a taparse.",
                now.minus(Duration.ofDays(15)));
        saveReport(u2, u8, req8, "MALA_CONDUCTA",
                "El techista dejó todo sucio y rompió una maceta sin avisar.",
                now.minus(Duration.ofDays(28)));

        // ============ MEDIA FILES ============
        saveMedia(u1, req1, "fotos/termotanque-antes.jpg", "image/jpeg", 2048576L, now.minus(Duration.ofDays(85)));
        saveMedia(u1, req1, "fotos/termotanque-despues.jpg", "image/jpeg", 1892345L, now.minus(Duration.ofDays(80)));
        saveMedia(u6, req4, "docs/certificado-gas.pdf", "application/pdf", 523456L, now.minus(Duration.ofDays(50)));
        saveMedia(u2, req7, "fotos/placard-terminado.jpg", "image/jpeg", 3124567L, now.minus(Duration.ofDays(33)));
        saveMedia(u4, req8, "fotos/filtracion-techo.jpg", "image/jpeg", 1567890L, now.minus(Duration.ofDays(35)));
    }

    // ===== helpers =====

    private Trade findByCode(List<Trade> list, String code) {
        return list.stream().filter(t -> t.getCode().equals(code)).findFirst().orElseThrow();
    }

    private User saveUser(int id, String name, boolean pro, boolean onboarding,
                          String dni, String locality, double lat, double lng, Instant created) {
        var u = User.builder()
                .email("usuario" + id + "@gmail.com")
                .passwordHash(encoder.encode("usuario" + id))
                .fullName(name)
                .phone(pro ? "+549341" + (4000000 + id * 10000) : "+549341" + (5000000 + id * 10000))
                .dniEncrypted(crypto.encrypt(dni))
                .locality(locality).latitude(lat).longitude(lng)
                .onboardingComplete(onboarding)
                .professionalEnabled(pro)
                .available(true)
                .createdAt(created)
                .build();
        return users.save(u);
    }

    private ServiceRequest saveRequest(User client, Trade trade, String title, String desc,
                                       String locality, double lat, double lng, Urgency urgency,
                                       BigDecimal budget, RequestStatus status, Instant created) {
        var r = ServiceRequest.builder()
                .client(client).trade(trade).title(title).description(desc)
                .locality(locality).latitude(lat).longitude(lng)
                .urgency(urgency).budget(budget).status(status)
                .createdAt(created)
                .build();
        return requests.save(r);
    }

    private ServiceApplication saveApplication(ServiceRequest request, User professional,
                                               String message, Instant created) {
        var a = ServiceApplication.builder()
                .request(request).professional(professional)
                .message(message)
                .createdAt(created)
                .build();
        return applications.save(a);
    }

    private void saveRating(ServiceRequest request, User author, User target,
                            short score, String comment, Instant created) {
        var r = Rating.builder()
                .request(request).author(author).target(target)
                .score(score).comment(comment)
                .createdAt(created)
                .build();
        ratings.save(r);
    }

    private void saveReport(User reporter, User reported, ServiceRequest request,
                            String reason, String details, Instant created) {
        var r = Report.builder()
                .reporter(reporter).reportedUser(reported).request(request)
                .reason(reason).details(details)
                .createdAt(created)
                .build();
        reports.save(r);
    }

    private void saveMedia(User owner, ServiceRequest request, String objectKey,
                           String contentType, long size, Instant created) {
        var m = MediaFile.builder()
                .owner(owner).request(request)
                .objectKey(objectKey).contentType(contentType).sizeBytes(size)
                .createdAt(created)
                .build();
        mediaFiles.save(m);
    }

    private User updateUserRating(User user, int newScore) {
        var total = user.getRating().multiply(BigDecimal.valueOf(user.getRatingCount()))
                .add(BigDecimal.valueOf(newScore));
        user.setRating(total.divide(BigDecimal.valueOf(user.getRatingCount() + 1), 1, java.math.RoundingMode.HALF_UP));
        user.setRatingCount(user.getRatingCount() + 1);
        return user;
    }
}
