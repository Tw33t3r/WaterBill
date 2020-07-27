import java.io.File;
import java.util.List;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Main {
	public static void main(String[] args) {
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
		String accNum = JOptionPane.showInputDialog("Enter Account Number");
		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			// setup webclient options
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
			webClient.getOptions().setThrowExceptionOnScriptError(true);
			webClient.setCssErrorHandler(new SilentCssErrorHandler());
			// get main page and forms
			final HtmlPage page = webClient.getPage("https://secure.phila.gov/WRB/WaterBill/account/GetAccount.aspx");
			webClient.waitForBackgroundJavaScript(10000);
			HtmlForm form1 = page.getForms().get(0);
			// get input buttons
			HtmlInput accountInput = form1.getInputByName("ctl00$MainContent$AcctNum");
			HtmlInput captchaInput = form1.getInputByName("ctl00$MainContent$CaptchaCodeTextBox");
			HtmlInput submitButton = form1.getInputByName("ctl00$MainContent$btnLookup");
			// get and save captcha
			HtmlImage image = (HtmlImage) page
					.getElementById("c_account_getaccount_maincontent_examplecaptcha_CaptchaImage");
			File captcha = new File("fileName.jpg");
			image.saveAs(captcha);
			accountInput.type(accNum);
			String captchaText = JOptionPane.showInputDialog("Enter Captcha");
			// input in fields
			captchaInput.type(captchaText);
			submitButton.click();
			// move to next page
			HtmlPage page2 = submitButton.click();
			HtmlForm form2 = page2.getForms().get(0);
			// get details of form submission
			List<DomElement> spans = page2.getElementsByTagName("span");
			String name = "";
			String accountNumber = "";
			String serviceAddress = "";
			String lastBill = "";
			String currentCharges = "";
			String lastPayment = "";
			String lastPaymentAmount = "";
			//search spans for values
			for (DomElement element : spans) {
				if (element.getAttribute("id").equals("MainContent_lblName")) {
					System.out.println("inside nameField");
					System.out.println(element.getChildNodes().get(0).getNodeValue());
					System.out.println(element.getTextContent());
					name = element.getChildNodes().get(0).getNodeValue();
				} else if (element.getAttribute("id").equals("MainContent_lblAcctNum")) {
					accountNumber = element.getTextContent();
				} else if (element.getAttribute("id").equals("MainContent_lblAddress")) {
					serviceAddress = element.getTextContent();
				} else if (element.getAttribute("id").equals("MainContent_lblLastBillDate")) {
					lastBill = element.getTextContent();
				} else if (element.getAttribute("id").equals("MainContent_lblCurrentCharges")) {
					currentCharges = element.getTextContent();
				} else if (element.getAttribute("id").equals("MainContent_lblLastPaymentDate")) {
					lastPayment = element.getTextContent();
				} else if (element.getAttribute("id").equals("MainContent_lblLastPaymentAmount")) {
					lastPaymentAmount = element.getTextContent();
				}
			}
			//output data
			System.out.println("Name: " + name);
			System.out.println("Account Number: " + accountNumber);
			System.out.println("Service Address: " + serviceAddress);
			System.out.println("Last Bill: " + lastBill);
			System.out.println("Current Charges: " + currentCharges);
			System.out.println("Last Payment: " + lastPayment);
			System.out.println("Last Payment Amount: " + lastPaymentAmount);



		} catch (Exception e) {
			System.out.println("Error Connecting");
			e.printStackTrace();
		}
	}
}