import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                        Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]), Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]), Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]), Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {
        Map<String, Integer> ptcpCountByInst = new HashMap<>();
        for(Course course : courses){
            String institution = course.institution;
            int participants = course.participants;
            ptcpCountByInst.put(institution, ptcpCountByInst.getOrDefault(institution, 0) + participants);
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(ptcpCountByInst.entrySet());
        Collections.sort(list, Map.Entry.comparingByKey());
        Map<String, Integer> fiPtcpCountByInst = new LinkedHashMap<>();
        for(Map.Entry<String, Integer> entry : list){
            fiPtcpCountByInst.put(entry.getKey(), entry.getValue());
        }
        return fiPtcpCountByInst;
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<String, Integer> ptcpCountByInstAndSubject = new HashMap<>();
        for(Course course : courses){
            String str = course.institution + "-" + course.subject;
            int participants = course.participants;
            ptcpCountByInstAndSubject.put(str, ptcpCountByInstAndSubject.getOrDefault(str, 0) + participants);
        }
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(ptcpCountByInstAndSubject.entrySet());
        Collections.sort(entries, Map.Entry.<String, Integer>comparingByValue().reversed());
        Map<String, Integer> sortedPtcpCountByInstAndSubject = new LinkedHashMap<>();
        for(Map.Entry<String, Integer> entry : entries){
            sortedPtcpCountByInstAndSubject.put(entry.getKey(), entry.getValue());
        }
        return sortedPtcpCountByInstAndSubject;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map <String, List<String>> indepentCourses = new HashMap<>();
        Map <String, List<String>> coDevelopCourses = new HashMap<>();
        Set <String> allInstructors = new HashSet<>();
        for(Course course : courses){
            String[] instructors = course.instructors.split(", ");
            for(String instructor : instructors){
                allInstructors.add(instructor);
                indepentCourses.putIfAbsent(instructor, new ArrayList<String>());
                coDevelopCourses.putIfAbsent(instructor, new ArrayList<String>());
                if(instructors.length == 1){
                    if(!indepentCourses.get(instructor).contains(course.title))
                        indepentCourses.get(instructor).add(course.title);
                } else{
                    if(!coDevelopCourses.get(instructor).contains(course.title))
                        coDevelopCourses.get(instructor).add(course.title);
                }
            }
        }
        for(Map.Entry<String, List<String>> entry : indepentCourses.entrySet()){
            Collections.sort(entry.getValue());
        }
        for(Map.Entry<String, List<String>> entry : coDevelopCourses.entrySet()){
            Collections.sort(entry.getValue());
        }
        Map <String, List<List<String>> > courseList = new LinkedHashMap<>();
        for(String instructor : allInstructors){
            List<List<String>> coursesOfInstructor = new ArrayList<>();
            coursesOfInstructor.add(indepentCourses.get(instructor));
            coursesOfInstructor.add(coDevelopCourses.get(instructor));
            courseList.put(instructor, coursesOfInstructor);
        }
//        for(Map.Entry<String, List<List<String>>> entry : courseList.entrySet()){
//            System.out.printf(entry.getKey() + " == " + entry.getValue().toString() + "\n");
//        }
        return courseList;
    }

    //4
    public List<String> getCourses(int topK, String by) {
        List<String> courseList = new ArrayList<>();
        switch (by) {
            case "hours":
                Map<String, Double> hour = new HashMap<>();
                for(Course course : courses){
                    if(hour.get(course.title) == null || hour.get(course.title) < course.totalHours){
                        hour.put(course.title, course.totalHours);
                    }
                }
                List<Map.Entry<String, Double>> list = new ArrayList<>(hour.entrySet());
                Collections.sort(list, Map.Entry.<String, Double>comparingByValue().reversed());
                for(int i = 1; i <= topK; i++){
                    courseList.add(list.get(i - 1).getKey());
//                    System.out.printf(courseList.get(i - 1) + " " + list.get(i - 1).getValue() + "\n");
                }
                return courseList;
            case "participants":
                Map<String, Integer> participant = new HashMap<>();
                for(Course course : courses){
                    if(participant.get(course.title) == null || participant.get(course.title) < course.participants)
                        participant.put(course.title, course.participants);
                }
                List<Map.Entry<String, Integer>> list1 = new ArrayList<>(participant.entrySet());
                Collections.sort(list1, Map.Entry.<String, Integer>comparingByValue().reversed());
                for(int i = 1; i <= topK; i++){
                    courseList.add(list1.get(i - 1).getKey());
//                    System.out.printf(courseList.get(i - 1) + " " + list1.get(i - 1).getValue() + "\n");
                }
                return courseList;
        }
        return null;
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        return courses.stream()
                .filter(course -> Arrays.stream(course.subject.replace(" ", "").split(',' + "|" + Pattern.quote("and"))).anyMatch(s -> s.toLowerCase().contains(courseSubject.replace(" ", "").toLowerCase())))
                .filter(course -> course.percentAudited >= percentAudited)
                .filter(course -> course.totalHours <= totalCourseHours)
                .sorted(Comparator.comparing(Course::getTitle))
                .map(Course::getTitle)
                .distinct()
                .collect(Collectors.toList());

    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        Map<String, List<Double>> courseCharacteristics = courses.stream()
                .collect(Collectors.groupingBy(
                        Course::getNumber,
                        Collectors.mapping(course -> new Double[] {
                                course.getMedianAge(),
                                course.getPercentMale(),
                                course.getPercentDegree()
                        }, Collectors.toList())
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().reduce((arr1, arr2) ->
                        new Double[]{
                                arr1[0] + arr2[0],
                                arr1[1] + arr2[1],
                                arr1[2] + arr2[2]
                        })
                        .map(arr -> Arrays.asList(
                                arr[0] / entry.getValue().size(),
                                arr[1] / entry.getValue().size(),
                                arr[2] / entry.getValue().size()
                        ))
                        .orElse(Collections.emptyList())));

        List<String> recommendCourse = courses.stream()
                .collect(Collectors.groupingBy(Course::getNumber))
                .entrySet().stream()
                .map(stringListEntry -> {
                    Course course = stringListEntry.getValue().stream()
                            .max(Comparator.comparing(Course::getLaunchDate))
                            .orElseThrow(NoSuchElementException::new);
                    List<Double> courseInfo = courseCharacteristics.getOrDefault(course.getNumber(), Collections.emptyList());
                    Double similarity = Math.pow(age - courseInfo.get(0), 2)
                            + Math.pow(gender * 100 - courseInfo.get(1), 2)
                            + Math.pow(isBachelorOrHigher * 100 - courseInfo.get(2), 2);
                    return new AbstractMap.SimpleEntry<>(course.getTitle(), similarity);
                })
                .sorted(Comparator.comparing((Map.Entry<String, Double> e) -> e.getValue()).thenComparing((Map.Entry<String, Double> e) -> e.getKey()))
                .map(Map.Entry::getKey)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
        return recommendCourse;
    }

}

class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) title = title.substring(1);
        if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
        this.title = title;
        if (instructors.startsWith("\"")) instructors = instructors.substring(1);
        if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
        this.instructors = instructors;
        if (subject.startsWith("\"")) subject = subject.substring(1);
        if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }
    public String getTitle(){return this.title;}
    public String getNumber(){return  this.number;}
    public double getMedianAge(){return this.medianAge;}
    public double getPercentMale(){return this.percentMale;}
    public double getPercentDegree(){return this.percentDegree;}
    public Date getLaunchDate(){return this.launchDate;}
}