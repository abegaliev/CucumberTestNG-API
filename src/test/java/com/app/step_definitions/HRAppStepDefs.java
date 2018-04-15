package com.app.step_definitions;

import static org.testng.Assert.assertEquals;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.app.pages.HRAppDeptEmpPage;
import com.app.utilities.BrowserUtils;
import com.app.utilities.ConfigurationReader;
import com.app.utilities.DBType;
import com.app.utilities.DBUtility;
import com.app.utilities.Driver;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class HRAppStepDefs {
	
	private WebDriver driver = Driver.getDriver();
	private HRAppDeptEmpPage deptEmpPage=new HRAppDeptEmpPage();
	
	private Map<String,String> UIDepartmentData = new HashMap<>();
	private Map<String,Object> DBDepartmentData = new HashMap<>();
	
	@Given("^I am on DeptEmpPage$")
	public void i_am_on_DeptEmpPage() {
		driver.get(ConfigurationReader.getProperty("hrapp.url"));
	}

	@When("^I search for department id (\\d+)$")
	public void i_search_for_department_id(int deptID) {
	    int currentDepId = Integer.parseInt(deptEmpPage.departmentID.getText());
	    
	    while(currentDepId != deptID) {
	    		deptEmpPage.Next.click();
	    		BrowserUtils.waitFor(2);
	    		deptEmpPage = new HRAppDeptEmpPage();
	    		BrowserUtils.waitForVisibility(deptEmpPage.departmentID, 5);
	    		currentDepId = Integer.parseInt(deptEmpPage.departmentID.getText());  		
	    }
	    //add UI data to hashmap
	    UIDepartmentData.put("DEPARTMENT_NAME", deptEmpPage.departmentName.getText());
	    UIDepartmentData.put("MANAGER_ID", deptEmpPage.managerID.getText());
	    UIDepartmentData.put("LOCATION_ID", deptEmpPage.locationID.getText());  
	}

	@When("^I query database with sql \"([^\"]*)\"$")
	public void i_query_database_with_sql(String sql) throws SQLException {
	    DBUtility.establishConnection(DBType.ORACLE);
	    List<Map<String,Object>> DBDataList = DBUtility.runSQLQuery(sql);
	    DBDepartmentData = DBDataList.get(0);
	    DBUtility.closeConnections();
	}

	@Then("^UI data and Database data must match$")
	public void ui_data_and_Database_data_must_match() {
		assertEquals(UIDepartmentData.get("DEPARTMENT_NAME"),DBDepartmentData.get("DEPARTMENT_NAME"));
		assertEquals(UIDepartmentData.get("LOCATION_ID"),String.valueOf(DBDepartmentData.get("LOCATION_ID")));
		assertEquals(UIDepartmentData.get("MANAGER_ID"),String.valueOf(DBDepartmentData.get("MANAGER_ID")));
	}

}