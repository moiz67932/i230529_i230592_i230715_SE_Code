import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class HashGen {
  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    System.out.println("ADMIN=" + encoder.encode("Admin123!"));
    System.out.println("REP=" + encoder.encode("Rep12345!"));
  }
}
