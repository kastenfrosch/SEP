package controller.gitlab;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import modal.ErrorModal;
import models.Group;
import models.Student;
import org.gitlab4j.api.CommitsApi;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.RepositoryApi;
import org.gitlab4j.api.models.AbstractUser;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Contributor;
import org.gitlab4j.api.models.Project;
import utils.TimeUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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

    private void drawCharts() throws GitLabApiException {
        drawPieChart();
        drawGraph();
    }

    private void drawPieChart() throws GitLabApiException {

        pieChart.getData().clear();

        if (selectedObject instanceof Group) {
            drawGroupPieChart((Group) selectedObject);
        }
        else if(selectedObject instanceof Student) {
            drawStudentPieChart((Student) selectedObject);
        }

        pieChart.setLegendVisible(true);
        pieChart.setLegendSide(Side.BOTTOM);

        pieChart.getData().forEach(data -> {
            data.nameProperty().bind(
                    Bindings.concat(
                            data.getName() + ": " + (int) data.getPieValue()
                    )
            );
        });
    }

    private void drawGroupPieChart(Group g) throws GitLabApiException {
        List<Commit> total = commitsApi.getCommits(project);

        for (Student s : g.getStudents()) {
            List<Commit> commits = new ArrayList<>();
            for (Commit c : total) {
                if (s.getPerson().getEmail().equals(c.getAuthorEmail())) {
                    commits.add(c);
                }
            }
            PieChart.Data d = new PieChart.Data(
                    String.format(
                            "%s. %s",
                            s.getPerson().getFirstname().substring(0, 1),
                            s.getPerson().getLastname()
                    ),
                    commits.size()
            );
            pieChart.getData().add(d);
        }

    }

    private void drawStudentPieChart(Student s) throws GitLabApiException {
        drawGroupPieChart(s.getGroup());
    }

    private void drawGraph() throws GitLabApiException {

        lineChart.getData().clear();

        if (selectedObject instanceof Group) {
            drawGroupGraph((Group) selectedObject);
        }
        else if(selectedObject instanceof Student) {
            drawStudentGraph((Student) selectedObject);
        }

        ((NumberAxis)lineChart.getXAxis()).setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number number) {
                if (!(number instanceof Long) && !(number instanceof Double)) {
                    throw new IllegalArgumentException("Number on chart x-axis should be of type Long");
                }
                if (number instanceof Double) {
                    number = Math.round((Double) number);
                }

                LocalDate date = LocalDate.ofEpochDay((long) number);

                return TimeUtils.toSimpleDateString(LocalDateTime.of(date, LocalTime.now()));
            }

            @Override
            public Number fromString(String s) {
                return TimeUtils.localDateFromString(s).toEpochDay();
            }
        });
    }

    private void drawGroupGraph(Group g) throws GitLabApiException {
        List<Commit> total = commitsApi.getCommits(project);
        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();

        Map<LocalDate, List<Commit>> commitMap = mapCommitsByDate(total);

        LocalDate[] boundaries = getBoundaries(commitMap.keySet());

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(boundaries[0].toEpochDay());
        xAxis.setUpperBound(boundaries[1].toEpochDay());


        XYChart.Series series = new XYChart.Series();
        series.setName("Commits over time");

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
        LocalDate[] boundaries = getBoundaries(commitMap.keySet());

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(boundaries[0].toEpochDay());
        xAxis.setUpperBound(boundaries[1].toEpochDay());

        XYChart.Series series = new XYChart.Series();
        series.setName("Commits over time");

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

    //returns smallest first, largest second
    private LocalDate[] getBoundaries(Set<LocalDate> keys) {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.of(1970, 1, 1);

        for(LocalDate d : keys) {
            if(d.isAfter(end)) {
                end = d;
            }
            if(d.isBefore(start)) {
                start = d;
            }
        }
        return new LocalDate[] {start, end};
    }

    public void setValues(Object g, GitLabApi api) {
        this.selectedObject = g;
        this.api = api;
        this.commitsApi = api.getCommitsApi();

        try {
            for (Project x : api.getProjectApi().getProjects()) {
                if (selectedObject instanceof Group) {
                    if (x.getHttpUrlToRepo().equals(((Group) selectedObject).getGitlabUrl())) {
                        project = x;
                        break;
                    }
                } else if (selectedObject instanceof Student) {
                    if(x.getHttpUrlToRepo().equals(((Student)selectedObject).getGroup().getGitlabUrl())) {
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


        try {
            drawCharts();
        } catch (GitLabApiException ex) {
            ErrorModal.show("Error", "Es ist ein unbekannter Fehler aufgetreten: " + ex.getMessage());
            return;
        }
    }
}
