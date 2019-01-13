package controller.gitlab;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class GitlabChartController {

    @FXML
    private TabPane chartTabPane;

    @FXML
    private Tab pieChartTab;

    @FXML
    private LineChart<?, ?> lineChart;

    @FXML
    private AnchorPane chartAnchorPane;

    @FXML
    private Tab lineChartTab;

    @FXML
    private PieChart pieChart;

}
