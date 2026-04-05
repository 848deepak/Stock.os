# Development & Extension Guide

This guide helps you extend and maintain the Inventory Management System.

## 🏗️ Adding New Features

### Example: Adding Category Management

#### Step 1: Create Entity
Create `src/main/java/com/inventory/model/Category.java`:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

#### Step 2: Create Repository
Create `src/main/java/com/inventory/repository/CategoryRepository.java`:

```java
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
}
```

#### Step 3: Create DTO
Create `src/main/java/com/inventory/dto/CategoryDTO.java`:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
}
```

#### Step 4: Create Service
Create `src/main/java/com/inventory/service/CategoryService.java`:

```java
@Service
@Transactional
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryDTO createCategory(CategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new InventoryException("Category already exists");
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setCreatedAt(LocalDateTime.now());

        Category saved = categoryRepository.save(category);
        return convertToDTO(saved);
    }

    private CategoryDTO convertToDTO(Category category) {
        return new CategoryDTO(
            category.getId(),
            category.getName(),
            category.getDescription()
        );
    }
}
```

#### Step 5: Create Controller
Create `src/main/java/com/inventory/controller/CategoryController.java`:

```java
@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO dto) {
        CategoryDTO created = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

#### Step 6: Run Tests
```bash
mvn test
```

---

## 🧑‍💻 Development Workflow

### Clean Architecture Checklist

When adding a new feature, ensure:

- [ ] Entity with JPA annotations
- [ ] Repository extending JpaRepository
- [ ] Service with @Transactional (read operations use readOnly = true)
- [ ] DTO for API contracts
- [ ] Controller with proper @PreAuthorize
- [ ] Global exception handling
- [ ] Input validation using @Valid
- [ ] Tests covering happy path and error scenarios

### Database Migrations

For schema changes, update `src/main/resources/schema.sql`:

```sql
-- Add new column
ALTER TABLE products ADD COLUMN supplier_name VARCHAR(100);

-- Add new table
CREATE TABLE suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100),
    phone VARCHAR(20)
);
```

Note: With `ddl-auto: update`, JPA auto-creates tables. Use Flyway for production migrations.

### Configuration

Add new config in `application.yml`:

```yaml
app:
  features:
    category_management: true
    barcode_scanning: false
  upload:
    max_file_size: 10MB
```

Access in code:
```java
@Value("${app.features.category_management}")
private boolean categoryManagementEnabled;
```

---

## 🚀 Performance Optimization

### 1. Add Database Indexes

```sql
CREATE INDEX idx_product_category ON products(category);
CREATE INDEX idx_transaction_product_id ON inventory_transactions(product_id);
```

### 2. Implement Caching

```java
@Service
public class ProductService {
    @Cacheable(value = "products", key = "#id")
    public ProductDTO getProduct(Long id) {
        // This will be cached
    }

    @CacheEvict(value = "products", key = "#id")
    public void updateProduct(Long id, ProductDTO dto) {
        // Cache invalidated on update
    }
}
```

Enable caching in `application.yml`:
```yaml
spring:
  cache:
    type: simple
```

### 3. Pagination Best Practices

```java
// Good - paginated
Page<Product> products = repository.findAll(PageRequest.of(0, 20));

// Bad - loading all records
List<Product> all = repository.findAll();
```

### 4. Lazy Loading

```java
// Fetch related data only when needed
@ManyToOne(fetch = FetchType.LAZY)
private Category category;

// Use @EntityGraph for specific queries
@EntityGraph(attributePaths = {"role"})
Optional<User> findByUsername(String username);
```

---

## 🧪 Testing

### Unit Test Example (Service)

```java
@SpringBootTest
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testCreateProduct() {
        ProductDTO input = new ProductDTO(/* ... */);
        Product expected = new Product(/* ... */);

        when(productRepository.save(any())).thenReturn(expected);

        ProductDTO result = productService.createProduct(input);

        assertEquals(expected.getName(), result.getName());
        verify(productRepository, times(1)).save(any());
    }
}
```

### Integration Test Example

```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetProduct() throws Exception {
        mockMvc.perform(get("/products/1")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Laptop"));
    }
}
```

---

## 📋 Frontend Extension

### Adding a New Page

1. **Create Page Component**
   ```javascript
   // src/pages/Reports.jsx
   export default function Reports() {
     const [data, setData] = useState(null)

     useEffect(() => {
       reportsService.getReport().then(res => setData(res.data))
     }, [])

     return <div>{/* JSX */}</div>
   }
   ```

2. **Add API Service**
   ```javascript
   // src/services/api.js - add:
   export const reportsService = {
     getReport: () => api.get('/reports'),
   }
   ```

3. **Update Router**
   ```javascript
   // src/App.jsx
   <Route path="reports" element={<Reports />} />
   ```

4. **Add Navigation Link**
   ```javascript
   // src/components/Layout.jsx - add to navItems:
   { path: '/reports', label: 'Reports', icon: BarChart3 }
   ```

---

## 🔐 Security Best Practices

### 1. Input Validation

```java
@Valid
@RequestBody
ProductDTO dto

// In DTO:
@NotBlank
@Size(min = 3, max = 100)
private String name;

@DecimalMin("0.1")
private BigDecimal price;
```

### 2. SQL Injection Prevention

```java
// ❌ Vulnerable
String query = "SELECT * FROM products WHERE name = '" + name + "'";

// ✅ Safe (using JPA)
repository.findByName(name);
```

### 3. Password Security

```java
// Always hash before storing
password = passwordEncoder.encode(rawPassword);

// Validate
if (!passwordEncoder.matches(rawPassword, storedHash)) {
    throw new AuthenticationException("Invalid password");
}
```

### 4. API Rate Limiting

```java
@RestController
@RateLimiter(name = "api")
public class ProductController { }
```

---

## 🐛 Debugging

### Enable Debug Logging

```yaml
logging:
  level:
    com.inventory: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type: TRACE
```

### Common Issues

**Issue**: JWT token expired
```
Solution: Clear localStorage and login again, or increase expiration in config
```

**Issue**: CORS errors in browser
```
Solution: Update CORS origins in SecurityConfig.java
```

**Issue**: Database connection failed
```
Solution: Check MySQL is running and credentials in application.yml
```

---

## 📚 Code Style Guidelines

### Backend (Java)

```java
// Classes and types: PascalCase
public class ProductService { }

// Methods and variables: camelCase
public ProductDTO getProductById(Long id) { }

// Constants: UPPER_SNAKE_CASE
private static final int MAX_RETRIES = 3;

// Use meaningful names
❌ List<Product> lop;
✅ List<Product> lowStockProducts;
```

### Frontend (JavaScript)

```javascript
// Components: PascalCase
function Dashboard() { }

// Functions and variables: camelCase
const handleSubmit = () => { }

// Constants: UPPER_SNAKE_CASE
const API_TIMEOUT = 5000
```

---

## 🚢 Deployment Checklist

Before deploying to production:

- [ ] Update `JWT_SECRET` to strong random value
- [ ] Set `spring.jpa.hibernate.ddl-auto: validate`
- [ ] Configure CORS for your domain
- [ ] Set up HTTPS/SSL certificates
- [ ] Enable database backups
- [ ] Configure logging (ELK stack preferred)
- [ ] Set up monitoring and alerts
- [ ] Load test the application
- [ ] Document API changes
- [ ] Set up CI/CD pipeline

---

## 📖 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [Tailwind CSS](https://tailwindcss.com)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [REST API Design](https://restfulapi.net)

---

## 🤝 Contributing

When contributing:
1. Follow the architecture pattern
2. Add tests for new features
3. Update documentation
4. Use meaningful commit messages
5. Create pull requests with detailed descriptions

Happy coding! 🎉

