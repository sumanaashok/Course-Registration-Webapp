import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

@WebServlet("/Main")
public class Main extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	static final long serialVersionUID = 1L;

	public Main() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		int radio = -1; // initial value of radio button (before selection)
		radio = Integer.parseInt(request.getParameter("rd"));
		String queryDB = "";
		String semester;

		try {
			// establishing a jdbc connection and connecting to the database 'NJIT'
			Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();

			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/NJIT?user=root&password=root");

			Statement stmt = conn.createStatement();
			// This is done to make sure that the database has the required values for
			// testing
			stmt.execute("DROP TABLE Courses");
			stmt.execute("CREATE TABLE IF NOT EXISTS Courses(courseID varchar(15), semester varchar(20), "
					+ "courseName varchar(50))");
			stmt.execute("Insert into courses values('CS670', 'Fall2021', 'Artificial Intelligence') ,"
					+ "('CS677', 'Fall2021', 'Deep Learning') , ('CS675', 'Spring2022', 'Machine Learning') ,"
					+ "('CS680', 'Spring2022', 'Linux Programming')");
			switch (radio) {
			case 0: {
				semester = request.getParameter("select semester");
				// the query to DB based on the semester selected
				if (semester.equals("Spring2022"))
					queryDB = "select * from Courses where semester='Spring2022'";
				else if (semester.equals("Fall2021"))
					queryDB = "select * from courses where semester='Fall2021'";

				// querying the DB and Displaying the result of the query in the form of a table
				ResultSet result = stmt.executeQuery(queryDB);
				out.print("<html>\n" + "<head><title> NJIT Registration System </title></head>\n"
						+ "<body style='background-image:linear-gradient(#ff002d, white)'>\n"
						+ "	<center><img alt=\"logo_image\"\n"
						+ "	src=\"https://www.njit.edu/sites/all/themes/corporate2018dev/logo.svg\"> </center>"
						+ "<table align=\"center\"  border = 1 width=50% height=30%>");
				out.print("<tr><th>Course ID</th><th>Semester</th><th>Course Name</th></tr>");

				while (result.next()) {
					out.print("<tr><td>" + result.getString(1) + "</td><td>" + result.getString(2) + "</td><td>"
							+ result.getString(3) + "</td></tr>");
				}

				out.print("</table>" + "</html></body>");

				stmt.close();
				break;
			}
			case 1: {
				String course_ID = request.getParameter("courseid");
				semester = request.getParameter("semester");
				String registration_res = "";

				// query DB to find if entered course id is offered for the semester entered
				if (!course_ID.equals("") && !semester.equals("")) {
					queryDB = "Select * from Courses where semester = \'" + semester + "\' and courseid = \'"+course_ID+"\'";
				}
				

					ResultSet result = stmt.executeQuery(queryDB);
					

				// only if the query returns a match display below success message
				if (result.next()) {
					String cName = result.getString("courseName");
					registration_res = "You are registered in " + cName + " for " + semester;

				}

				// this is the case when course id or semester entered does not return a match
				else {

					if (!course_ID.equals("") && !semester.equals(""))

						registration_res = "The course is not offered.";
				}

				// Displaying message on client based on match or no match
				// on the course id and semester values entered.
				out.println("<html>\n" + "<head><title> NJIT Registration System </title></head>\n"
						+ "<body style='background-image:linear-gradient(#ff002d, white)'>\n"
						+ "	<center><img alt=\"logo_image\"\n"
						+ "	src=\"https://www.njit.edu/sites/all/themes/corporate2018dev/logo.svg\"> </center>"
						+ "<h1 align=\"center\"> <font color='black'>" + registration_res + "</font></h1>\n"
						+ "<br><br><br>" + "</body></html>");
				stmt.close();
				}
				
				
				break;
			
			}

			conn.close();
		}

		// When query fails the error is being caught by this catch block
		catch (Exception e) {
			System.out.println(e);

			out.println("<html>\n" + "<head><title> NJIT Registration System  </title></head>\n"
					+ "<body style='background-image:linear-gradient(#ff002d, white)'>\n"
					+ "	<center><img alt=\"logo_image\"\n"
					+ "	src=\"https://www.njit.edu/sites/all/themes/corporate2018dev/logo.svg\"> </center>");

			// message displayed on client when course id or semester left empty on
			// registration page
			if (radio == 1) {
				out.println("<h2 align=\"center\"> <font color=\"black\">"
						+ "Course ID and/or semester should not be empty." + "</h2>\n");
				out.println(
						"<h2 align=\"center\"> <font color=\"black\">" + "Please enter required details." + "</h2>\n");
			}
			// message displayed when semester is not selected on the search pages
			else
				out.println("<h1 align=\"center\"> <font color=\"black\">" + "Please Select A Semester." + "</h1>");

			out.println("</body></html>");

		}

	}

}
