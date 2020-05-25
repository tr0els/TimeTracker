/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JUnitTesting;

import java.security.NoSuchAlgorithmException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import timetracker.BE.User;
import timetracker.BLL.InputValidator;
import timetracker.DAL.DALException;

/**
 *
 * @author Draik
 */
public class ValidatorTest {
    
    public ValidatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void testEmailValidationHotmail(){
        assertTrue(InputValidator.valEmail("Karl@hotmail.com"));
    }
    
    @Test
    public void testEmailValidationOutlook(){
        assertTrue(InputValidator.valEmail("Karl@outlook.dk"));
    }
    
    @Test
    public void testNotValidEmail(){
        assertFalse(InputValidator.valEmail("Benny"));
    }
    
    @Test
    public void testNotValidEmail1(){
        assertFalse(InputValidator.valEmail("@hotmail.com"));
    }
    
    @Test
    public void testNotValidEmail2(){
        assertFalse(InputValidator.valEmail("Benny@hotmail"));
    }
    
    @Test
    public void testNotValidEmail3(){
        assertFalse(InputValidator.valEmail("Benny.com"));
    }
    
    @Test
    public void testNameValidator(){
        assertTrue(InputValidator.valName("Bent"));
    }
    
    @Test
    public void testNameValidatorIncorrectNumber(){
        assertFalse(InputValidator.valName("Bent2"));
    }
    
    @Test
    public void testNameValidatorIncorrectSpaces(){
        assertFalse(InputValidator.valName("Bent Bentersen"));
    }
    
    @Test
    public void testLoginSuccesfully() throws NoSuchAlgorithmException, DALException{
        InputValidator Instance = InputValidator.getInstance();
        
        User userTest = new User(24, "Admin", "Account", "Admin@email.com", 1, 1);
        
        assertEquals(userTest, Instance.login("Admin@email.com", "1234"));
    }
    
    @Test
    public void testLoginUnsuccesfully() throws NoSuchAlgorithmException, DALException{
        InputValidator Instance = InputValidator.getInstance();
        
        assertNull(Instance.login("emailThatIsNotInDataBase", "PasswordThatIsNotInDataBase"));
    }
    
    @Test
    public void testIfEmailAlreadyExistSucces() throws DALException{
        InputValidator Instance = InputValidator.getInstance();
        
        assertTrue(Instance.valExistingEmail("newAndExcitingEmail@email.com"));
    }
    
    @Test
    public void testIfEmailAlreadyExistFail() throws DALException{
        InputValidator Instance = InputValidator.getInstance();
        
        assertFalse(Instance.valExistingEmail("Admin@email.com"));
    }
    
    @Test
    public void testIfEmailAlreadyExistEditSucces() throws DALException{
        InputValidator Instance = InputValidator.getInstance();
        
        assertTrue(Instance.valExistingEmailEdit(24, "Admin@email.com"));
    }
    
    @Test
    public void testIfEmailAlreadyExistEditFail() throws DALException{
        InputValidator Instance = InputValidator.getInstance();
        
        assertFalse(Instance.valExistingEmailEdit(24, "Bruger@email.com"));
    }
    

}
