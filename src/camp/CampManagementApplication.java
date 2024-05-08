package camp;

import camp.model.Score;
import camp.model.Student;
import camp.model.Subject;
import camp.store.StudentStore;
import camp.store.SubjectStore;
import camp.store.ScoreStore;

import java.util.*;

public class CampManagementApplication {
    private static Scanner sc;
    private final StudentStore studentStore;
    private final SubjectStore subjectStore;
    private final ScoreStore scoreStore;


    public CampManagementApplication() {
        this.studentStore = new StudentStore();
        this.subjectStore = new SubjectStore();
        this.scoreStore = new ScoreStore();
        sc = new Scanner(System.in);
    }

    public void displayMainView() throws InterruptedException {
        boolean flag = true;
        while (flag) {
            System.out.println("\n==================================");
            System.out.println("내일배움캠프 수강생 관리 프로그램 실행 중...");
            System.out.println("1. 수강생 관리");
            System.out.println("2. 점수 관리");
            System.out.println("3. 프로그램 종료");
            System.out.print("관리 항목을 선택하세요...");
            System.out.println();
            int input = sc.nextInt();

            switch (input) {
                case 1 -> displayStudentView(); // 수강생 관리
                case 2 -> displayScoreView(); // 점수 관리
                case 3 -> flag = false; // 프로그램 종료
                default -> {
                    System.out.println("잘못된 입력입니다.\n되돌아갑니다!");
                    Thread.sleep(2000);
                }
            }
        }
        System.out.println("프로그램을 종료합니다.");
    }

    private void displayStudentView() {
        while (true) {
            System.out.println("==================================");
            System.out.println("수강생 관리 실행 중...");
            System.out.println("1. 수강생 등록");
            System.out.println("2. 수강생 목록 조회");
            System.out.println("3. 메인 화면 이동");
            System.out.print("관리 항목을 선택하세요...");
            System.out.println();
            int input = sc.nextInt();

            switch (input) {
                case 1 -> createStudent(); // 수강생 등록
                case 2 -> inquireStudent(); // 수강생 목록 조회
                case 3 -> {
                    return;
                }
                default -> {
                    System.out.println("잘못된 입력입니다.\n메인 화면 이동...");
                    return;
                }
            }
        }
    }

    // 수강생 등록
    private void createStudent() {
        System.out.println("\n수강생을 등록합니다...");
        //
        System.out.print("수강생 이름 입력: ");
        String studentName = sc.next();

        // 기능 구현 (필수 과목, 선택 과목)
        List<Subject> selectedSubjects = selectSubjects();

        if (selectedSubjects == null) {
            System.out.println("과목 선택이 올바르지 않습니다. 등록을 취소합니다.");
            return;
        }

        Student student = new Student(studentName, selectedSubjects); // 수강생 인스턴스 생성 코드

        // is valid subject?
        if (!student.isValidSubjects()) {
            System.out.println("과목 선택을 잘못하셨습니다");
            return;
        }

        this.studentStore.save(student);

        System.out.println("수강생 등록 성공!\n");
    }

    private List<Subject> selectSubjects() {
        System.out.println("과목 선택: 최소 3개의 필수과목과 2개의 선택과목을 선택해야 합니다.");
        List<Subject> all = this.subjectStore.findAll();

        // TODO(민혁님)
        // 필수과목: id)name id)name id)name id)name
        System.out.println("필수 과목: ");
        List<Subject> mandatoryList = all.stream().filter(Subject::isMandatory).toList();
        for (Subject subject : mandatoryList) {
            System.out.printf("%s) %s ", subject.getSubjectId(), subject.getSubjectName());
        }
        System.out.println("\n선택 과목: ");
        List<Subject> choiceList = all.stream().filter(Subject::isChoice).toList();
        for (Subject subject : choiceList) {
            System.out.printf("%s) %s ", subject.getSubjectId(), subject.getSubjectName());
        }

        System.out.print("\n선택할 과목의 번호를 입력하세요 (예: 1 2 3 6 7): ");
        sc.nextLine(); // Buffer Clear
        String[] inputs = sc.nextLine().split(" ");

        List<Subject> selectedSubjects = new ArrayList<>();

        for (String number : inputs) {
            Optional<Subject> subjectOptional = this.subjectStore.findById(number);
            if (subjectOptional.isEmpty()) {
                System.out.println("잘못된 과목 번호입니다: " + number);
                return null;
            }
            selectedSubjects.add(subjectOptional.get());
        }
        return selectedSubjects;
    }

    // TODO: 꾸미기(성훈님)
    private void inquireStudent() {
        // 목록 조회
        List<Student> students = this.studentStore.findAll();
        // string format
        System.out.printf("%5s | %5s\n", "고유번호", "이  름");
        for (Student student : students) {
            System.out.printf("%6s번 | %4s\n", student.getStudentId(), student.getStudentName());
        }
    }

    private  void displayScoreView() {
        boolean flag = true;
        while (flag) {
            System.out.println("==================================");
            System.out.println("점수 관리 실행 중...");
            System.out.println("1. 수강생의 과목별 시험 회차 및 점수 등록");
            System.out.println("2. 수강생의 과목별 회차 점수 수정");
            System.out.println("3. 수강생의 특정 과목 회차별 등급 조회");
            System.out.println("4. 메인 화면 이동");
            System.out.print("관리 항목을 선택하세요...");
            int input = sc.nextInt();

            switch (input) {
                case 1 -> createScore(); // 수강생의 과목별 시험 회차 및 점수 등록
                case 2 -> updateRoundScoreBySubject(); // 수강생의 과목별 회차 점수 수정
                case 3 -> inquireRoundGradeBySubject(); // 수강생의 특정 과목 회차별 등급 조회
                case 4 -> flag = false; // 메인 화면 이동
                default -> {
                    System.out.println("잘못된 입력입니다.\n메인 화면 이동...");
                    flag = false;
                }
            }
        }
    }

    //수강생 번호 입력
    private static String getStudentId() {
        System.out.print("\n관리할 수강생의 번호를 입력하시오...");
        return sc.next();
    }

    //범위내 회차 입력 (1~10)
    public static int getRightScoreId(){
        int scoreId;
        while (true) {
            System.out.print("회차를 입력해주세요: ");
            scoreId = sc.nextInt();
            sc.nextLine();//buffer cleaner

            if(0 < scoreId && scoreId<11){
                return scoreId;
            }
            System.out.println("회차는 1~10까지 입력할 수 있습니다.");
        }
    }
    //범위내 과목id 입력 (1 ~9)
    public int getRightSubjectId() {
        int subjectId;
        while (true) {
            System.out.print("과목id를 입력해주세요: ");
            subjectId = sc.nextInt();
            sc.nextLine();//buffer cleaner

            if(0 < subjectId && subjectId<10){
                return subjectId;
            }
            System.out.println("과목id는 1~9까지 입력할 수 있습니다.");
        }
    }
    //범위내 점수 입력 (1 ~ 100)
    public int getRightScore() {
        int score;
        while (true) {
            System.out.print("시험점수를 입력해주세요: ");
            score = sc.nextInt();
            sc.nextLine();//buffer cleaner

            if(0 < score && score<101){
                return score;
            }
            System.out.println("점수는 1~100점까지만 입력 할 수 있습니다.");
        }
    }
    public int regetRightScore() {
        int score;
        while (true) {
            System.out.print("시험점수를 입력해주세요: ");
            score = sc.nextInt();
            sc.nextLine();//buffer cleaner

            if(0 < score && score<101){
                return score;
            }
            System.out.println("점수는 1~100점까지만 입력 할 수 있습니다.");
        }
    }

    //등급
    public char getGrade(int subjectId ,int score){
        int grade_score = score;
        Character grade = ' ';
        if(subjectId >5) {//6-9 선택
            if (grade_score > 89) {
                return grade = 'A';
            } else if (90 > grade_score && grade_score > 79) {
                return grade = 'B';
            } else if (80 > grade_score && grade_score > 69) {
                return grade = 'C';
            } else if (70 > grade_score && grade_score > 59) {
                return grade = 'D';
            } else if (60 > grade_score && grade_score > 49) {
                return grade = 'F';
            } else {
                return grade = 'N';
            }
        }else {//1-5 필수
            if (grade_score > 94) {
                return grade = 'A';
            } else if (grade_score > 89) {
                return grade = 'B';
            } else if (grade_score > 79) {
                return grade = 'C';
            } else if ( grade_score > 69) {
                return grade = 'D';
            } else if (grade_score > 59) {
                return grade = 'F';
            } else {
                return grade = 'N';
            }
        }
    }

    // 수강생의 과목별 시험 회차 및 점수 등록
    private void createScore() {
        String studentId = getStudentId(); // 관리할 수강생 고유 번호
        System.out.println("시험 점수를 등록합니다...");
        // 기능 구현
        // String(key) = StudentId, arraylist(value) = 회차,과목ID, 시험점수, 등급
        //value  Score => 회차1 과목java 점수50 등급C
        // Score => 회차2 과목Spring 점수80 등급B
        // scores.get(studentId)
        //key (studentId) => key
        // 등록되지 않은 student id처리
        // scores.containsKey(studentId) == key값이 1이 있냐-> T==> 점수 등록할수있게/ F ==> 등록이 안된 사용자입니다(key new)

        //id : key값 없으면 새로 생성
        // if containsKey(key) 확인
        scoreStore.checkExistKey(studentId);


        //회차입력 (1~10범위만 입력받게)
        int scoreId = getRightScoreId();

        //과목입력 (1~9범위만 입력)
        int subjectId = getRightSubjectId();

        //점수입력 (1~100 범위만 입력)
        int score = getRightScore();

        //점수에 따른 등급 생성
        char grade = getGrade(subjectId ,score);


        //Score 인스턴스생성 <= 입력받은 값 다 넣기
        Score studentScore = new Score(scoreId, subjectId, score, grade);

        // ScoreStore 저장
        scoreStore.save(studentId, studentScore); //ggumi : 얘랑 ScoreStore class에서 18번째줄 저장 함수 추가햇어여

        //출력 확인
        //scoreStore.findAll() => return Hashmap

        for(int i =0; i < scoreStore.findAll().get(studentId).size();i++){
         System.out.println("회차 : " + scoreStore.findAll().get(studentId).get(i).getScoreId());
        }

        System.out.println("\n점수 등록 성공!");
    }

    // 수강생의 과목별 회차 점수 수정
    private void updateRoundScoreBySubject() {
        HashMap<String,ArrayList<Score>> students = this.scoreStore.findAll();
        String studentId = getStudentId(); // 관리할 수강생 고유 번호
        // 기능 구현 (수정할 과목 및 회차, 점수)
        // 회차 입력
        int scoreId = getRightScoreId();
        //과목입력
        int subjectId = getRightSubjectId();
        //점수 수정값 들어가야함
        int newScore = regetRightScore();


        //해당하는 과목, 회차의 점수 수정하기
        for(int i =0; i < students.get(studentId).size();i++){
                    //과목명
                    //수정??
                    //students.get(studentId).get(i).

                    //Hash map
                    //1 -> [Score{회차, 과목, 점수, 등급},
                    //      Score{회차, 과목, 점수, 등급},
                    //      Score{회차, 과목, 점수, 등급}]
                    //score1.getScore()// 과거 점수

                    //students.get(studentId).get(i).getsubjectId().setScore(newScore);
                    //

                    for(Score score1 : students.get(studentId)){
                        if(score1.getScoreId() == (scoreId) && score1.getsubjectId() == (subjectId)){
                            score1.setScore(newScore); //setScore로 해보세여 !!
                            char newGrade = getGrade(subjectId ,newScore);
                            score1.setGrade(newGrade);
                            break;
                        }
                    }
        }



        //


        System.out.println("시험 점수를 수정합니다...");
        // 기능 구현
        System.out.println("\n점수 수정 성공!");
    }

    // 수강생의 특정 과목 회차별 등급 조회
    private void inquireRoundGradeBySubject() {
        HashMap<String,ArrayList<Score>> students = this.scoreStore.findAll();

        String studentId = getStudentId(); // 관리할 수강생 고유 번호
        // 기능 구현 (조회할 특정 과목)
        System.out.println("특정 과목의 등급을 조회합니다...");
        int subjectId = getRightSubjectId();

        //선택한 과목 id만 출력하도록 수정
        for(int i =0; i < students.get(studentId).size();i++){
            System.out.println(students.get(studentId).get(i).getScoreId() + "회차 "  + "등급 : " + students.get(studentId).get(i).getGrade());
        }

        System.out.println("회차별 등급을 조회합니다...");
        // 기능 구현



        System.out.println("\n등급 조회 성공!");
    }

}