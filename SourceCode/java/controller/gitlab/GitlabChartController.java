package controller.gitlab;

import com.sun.javafx.charts.Legend;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import modal.ErrorModal;
import models.Group;
import models.Groupage;
import models.Student;
import org.gitlab4j.api.CommitsApi;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import utils.TimeUtils;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class GitlabChartController {

    @FXML
    private TabPane chartTabPane;

    @FXML
    private Tab pieChartTab;

    @FXML
    private LineChart<Number, Integer> lineChart;

    @FXML
    private AnchorPane chartAnchorPane;

    @FXML
    private Tab lineChartTab;

    @FXML
    private PieChart pieChart;

    private Object selectedObject;
    private GitLabApi api;
    private CommitsApi commitsApi;
    private Project project;

    public void setValues(Object g, GitLabApi api) {
        this.selectedObject = g;
        this.api = api;
        this.commitsApi = api.getCommitsApi();
        if (!(selectedObject instanceof Groupage)) {
            try {
                for (Project x : api.getProjectApi().getProjects()) {
                    if (selectedObject instanceof Group) {
                        if (x.getHttpUrlToRepo().equals(((Group) selectedObject).getGitlabUrl())) {
                            project = x;
                            break;
                        }
                    } else if (selectedObject instanceof Student) {
                        if (x.getHttpUrlToRepo().equals(((Student) selectedObject).getGroup().getGitlabUrl())) {
                            project = x;
                            break;
                        }
                    }
                }
                if (project == null) {
                    ErrorModal.show("Das Gruppenrepository wurde nicht gefunden.");
                    return;
                }
            } catch (GitLabApiException ex) {
                ErrorModal.show("Das Gruppenrepository wurde nicht gefunden.");
                return;
            }
        }

        try {
            drawCharts();
        } catch (GitLabApiException ex) {
            ErrorModal.show("Error", "Es ist ein unbekannter Fehler aufgetreten: " + ex.getMessage());
            return;
        }
    }

    private void drawCharts() throws GitLabApiException {
        drawPieChart();
        drawGraph();
    }

    private void drawPieChart() throws GitLabApiException {

        pieChart.getData().clear();

        if (selectedObject instanceof Groupage) {
            drawGroupagePieChart((Groupage) selectedObject);
        } else if (selectedObject instanceof Group) {
            drawGroupPieChart((Group) selectedObject);
        } else if (selectedObject instanceof Student) {
            drawGroupPieChart(((Student) selectedObject).getGroup());
        }

        pieChart.setLegendVisible(true);
        pieChart.setLegendSide(Side.BOTTOM);

        int totalCommits = 0;
        for(PieChart.Data data: pieChart.getData()) {
            totalCommits += (int)data.getPieValue();
            data.setName(data.getName() + ": " + (int) data.getPieValue());
        }
        final int finalTotalCommits = totalCommits;

        Label percentage = new Label("");
        Tooltip tooltip = new Tooltip();
        tooltip.setGraphic(percentage);

        for (PieChart.Data data : pieChart.getData()) {
            data.getNode().setOnMousePressed(me -> {
                            percentage.setText(String.valueOf(new DecimalFormat("#.##").format((data.getPieValue() / finalTotalCommits) * 100)) + "%");
                            tooltip.show(chartAnchorPane.getScene().getRoot(), me.getScreenX() - 20, me.getScreenY() - 20);
                    });
            data.getNode().setOnMouseReleased(me -> {
                            tooltip.hide();
            });
        }
    }

    private void drawGroupagePieChart(Groupage g) throws GitLabApiException {
        for (Group group : g.getGroups()) {
            project = null;
            for (Project x : api.getProjectApi().getProjects()) {
                if (x.getHttpUrlToRepo().equals(group.getGitlabUrl())) {
                    project = x;
                    break;
                }
            }

            if (project != null) {
                List<Commit> total = commitsApi.getCommits(project);
                int count = 0;
                for (Student s : group.getStudents()) {
                    for (Commit c : total) {
                        if (s.getPerson().getEmail().equals(c.getAuthorEmail())) {
                            count++;
                        }
                    }
                }
                PieChart.Data d = new PieChart.Data(
                        group.getName(),
                        count
                );
                pieChart.getData().add(d);
            }
        }
    }

    private void drawGroupPieChart(Group g) throws GitLabApiException {
        List<Commit> total = commitsApi.getCommits(project);

        for (Student s : g.getStudents()) {
            int count = 0;
            for (Commit c : total) {
                if (s.getPerson().getEmail().equals(c.getAuthorEmail())) {
                    count++;
                }
            }
            PieChart.Data d = new PieChart.Data(
                    s.getPerson().getFirstname().substring(0, 1) + ". " + s.getPerson().getLastname(),
                    count
            );
            pieChart.getData().add(d);
        }
    }

    private void drawGraph() throws GitLabApiException {

        lineChart.getData().clear();

        if (selectedObject instanceof Groupage) {
            drawGroupageGraph((Groupage) selectedObject);
        } else if (selectedObject instanceof Group) {
            for (Student s : ((Group) selectedObject).getStudents()) {
                drawStudentGraph(s);
            }
        } else if (selectedObject instanceof Student) {
            drawStudentGraph((Student) selectedObject);
        }

        long minTime = LocalDate.now().toEpochDay();
        long maxTime = LocalDate.of(1970, 1, 1).toEpochDay();

        for (var series : lineChart.getData()) {
            for (var data : series.getData()) {
                if (((long) data.getXValue()) < minTime) {
                    minTime = (long) data.getXValue();
                }
                if (((long) data.getXValue()) > maxTime) {
                    maxTime = (long) data.getXValue();
                }
            }
        }

        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();

        xAxis.setAutoRanging(false);
        xAxis.setUpperBound(maxTime);
        xAxis.setLowerBound(minTime);

        xAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number number) {
                if (number instanceof Double) {
                    number = Math.round((Double) number);
                }

                LocalDate date = LocalDate.ofEpochDay((long) number);

                return date.getDayOfMonth() + "." + date.getMonthValue();
            }

            @Override
            public Number fromString(String s) {
                return TimeUtils.localDateFromString(s).toEpochDay();
            }
        });

        for (Node n : lineChart.getChildrenUnmodifiable()) {
            if (n instanceof Legend) {
                Legend l = (Legend) n;
                for (Legend.LegendItem li : l.getItems()) {
                    for (XYChart.Series<Number, Integer> s : lineChart.getData()) {
                        if (s.getName().equals(li.getText())) {
                            li.getSymbol().setCursor(Cursor.HAND);
                            li.getSymbol().setOnMouseClicked(me -> {
                                    s.getNode().setVisible(!s.getNode().isVisible());
                                    for (XYChart.Data<Number, Integer> d : s.getData()) {
                                        if (d.getNode() != null) {
                                            d.getNode().setVisible(s.getNode().isVisible());
                                        }
                                    }
                            });
                            break;
                        }
                    }
                }
            }
        }
    }

    private void drawGroupageGraph(Groupage groupage) throws GitLabApiException {
        for (Group group : groupage.getGroups()) {
            project = null;
            for (Project x : api.getProjectApi().getProjects()) {
                if (x.getHttpUrlToRepo().equals(group.getGitlabUrl())) {
                    project = x;
                    break;
                }
            }
            if (project != null) {
                drawGroupGraph(group);
            }
        }
    }

    private void drawGroupGraph(Group g) throws GitLabApiException {
        List<Commit> total = commitsApi.getCommits(project);
        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();

        Map<LocalDate, List<Commit>> commitMap = mapCommitsByDate(total);

        XYChart.Series series = new XYChart.Series();
        series.setName(g.getName());

        for (Map.Entry<LocalDate, List<Commit>> entry : commitMap.entrySet()) {
            series.getData().add(new XYChart.Data(entry.getKey().toEpochDay(), entry.getValue().size()));
        }
        lineChart.getData().add(series);
    }

    private void drawStudentGraph(Student s) throws GitLabApiException {
        List<Commit> total = commitsApi.getCommits(project);
        total.removeIf(commit -> !(commit.getAuthorEmail().equals(s.getPerson().getEmail())));

        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();

        Map<LocalDate, List<Commit>> commitMap = mapCommitsByDate(total);

        XYChart.Series series = new XYChart.Series();
        series.setName(s.getPerson().getFirstname().substring(0, 1) + ". " + s.getPerson().getLastname());

        for (Map.Entry<LocalDate, List<Commit>> entry : commitMap.entrySet()) {
            series.getData().add(new XYChart.Data(entry.getKey().toEpochDay(), entry.getValue().size()));
        }
        lineChart.getData().add(series);
    }

    private Map<LocalDate, List<Commit>> mapCommitsByDate(List<Commit> total) {
        Map<LocalDate, List<Commit>> commitMap = new HashMap<>();
        for (Commit c : total) {
            LocalDate date = LocalDate.ofInstant(c.getCommittedDate().toInstant(), ZoneId.systemDefault());
            if (!commitMap.containsKey(date)) {
                commitMap.put(date, new LinkedList<>());
                commitMap.get(date).add(c);
            } else {
                commitMap.get(date).add(c);
            }
        }
        return commitMap;
    }
}
