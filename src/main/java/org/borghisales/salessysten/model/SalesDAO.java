package org.borghisales.salessysten.model;

import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import org.borghisales.salessysten.controllers.MainController;
import org.borghisales.salessysten.controllers.MenuController;

import java.sql.*;

public class SalesDAO {

    public int IdSale(){
        String sql = "SELECT MAX(idSales) FROM sales";
        try (Connection conn = DBConnection.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            try (ResultSet rs = pstmt.executeQuery()){
                if (rs.next()){
                    if (rs.getInt(1)==0)
                        return 1;

                    return rs.getInt(1);
                }
                return 1;
            }

        }catch (SQLException e){
            MenuController.setAlert(Alert.AlertType.ERROR, "Error searching IdSale: " + e.getMessage());
            return 1;
        }
    }
    public boolean SaveSale(Sales sale){
        String sql = "INSERT INTO sales (idCustomer,idSeller,numberSales,saleDate,amount,state) values(?,?,?,?,?,?)";

        try (Connection conn = DBConnection.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,sale.idCustomer());
            pstmt.setInt(2,sale.idSeller());
            pstmt.setString(3,sale.numberSales());
            pstmt.setDate(4, Date.valueOf(sale.saleDate()));
            pstmt.setDouble(5,sale.amount());
            pstmt.setString(6,sale.state().name());

            int rows_affected = pstmt.executeUpdate();

            if (rows_affected>0){
                MenuController.setAlert(Alert.AlertType.CONFIRMATION, "Sale saved correctly");
                return true;
            }else{
                MenuController.setAlert(Alert.AlertType.ERROR, "Error saving sale: ");
                return false;
            }

        }catch (SQLException e){
            MenuController.setAlert(Alert.AlertType.ERROR, "Error saving sale: " + e.getMessage());
            return false;
        }

    }

    public boolean SaveDetailsSale(ObservableList<ShoppingCart> products, int id){

        try (Connection conn = DBConnection.connection()){

            for (ShoppingCart e:products) {
                String sql = "INSERT INTO sales_details (idSales,idProduct,quantity, priceSale) values(?,?,?,?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, id);
                    pstmt.setInt(2, Integer.parseInt(e.cod()));
                    pstmt.setInt(3, e.quantity());
                    pstmt.setDouble(4, e.price());

                    pstmt.executeUpdate();

//                    int rows_affected = pstmt.executeUpdate();

//                    if (rows_affected > 0) {
//                        MenuController.setAlert(Alert.AlertType.CONFIRMATION, "Sale details saved correctly");
//                        return true;
//                    } else {
//                        MenuController.setAlert(Alert.AlertType.ERROR, "Error saving sale details : ");
//                        return false;
//                    }
                }
            }
                return true;



        }catch (SQLException e){
            MenuController.setAlert(Alert.AlertType.ERROR, "Error saving sale details: " + e.getMessage());
            return false;
        }

    }

    public void setTable(ObservableList<Sales> sales) {
        String sql = "SELECT * FROM sales WHERE idSeller = ?";

        try (Connection conn = DBConnection.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, MainController.sellerLog.idSeller());

            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()){
                    Sales sale = Sales.fromResultSet(rs);
                    sales.add(sale);
                }
            }

        }catch (SQLException e){
            MenuController.setAlert(Alert.AlertType.ERROR, "Error searching sales : " + e.getMessage());
        }


    }

}