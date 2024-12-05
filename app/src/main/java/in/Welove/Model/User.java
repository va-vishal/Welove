package in.Welove.Model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class User {
    public String id;
    public String name;
    public String email;
    public String age;
    public String description;
    public String dob;
    public String educationLevel;
    public String gender;
    public String imageurl;
    public String imageurl1;
    public String imageurl2;
    public String imageurl3;
    public String imageurl4;
    public String imageurl5;
    public String jobType;
    public String area;
    public String state;
    public String maritalStatus;
    public String motherTongue;
    public String personality;
    public String religion;
    public String prefGender;
    private String lastSeen;
    private String phoneNumber;
    private String password;
    private String fcmToken;
    private Boolean hideAge;
    private Boolean hideName;
    private Boolean hideProfile;
    private Boolean hideLocation;
    private Boolean typing;
    private double latitude;
    private double longitude;
    private int maxDistance;
    private int ageRange;
    private Boolean isLocationHidden;
    private Boolean isAgeHidden;
    private Boolean isNameHidden;
    private Boolean isProfileHidden;
    private Boolean pp;
    private Boolean p;
    private String premiumEndDate;
    private String premiumStartDate;
    private Boolean hot;
    private Boolean completeprofile;
    private Boolean hotProfieOrNormalProfile;
    private Boolean hotProfile;
    private String hotStartDate;
    private String hotEndDate;
    private Boolean status;
    private Map<String, Boolean> likesList;  // Use Map instead of List
    private Map<String, Boolean> visitsList;
    private Map<String, Boolean> blockedList;
    private Map<String, Boolean> likedList;
    private Map<String, Boolean> dislikedList;
    private Map<String, Boolean> matches;
    private List<String> notifications;
    private double walletBalance;
    private Long deletionRequestedAt;



    public User() {
    }

    public User(String id, String name, String email, String age, String description, String dob, String educationLevel, String gender, String imageurl, String imageurl1, String imageurl2, String imageurl3, String imageurl4, String imageurl5, String jobType, String area, String state, String maritalStatus, String motherTongue, String personality, String religion, String prefGender, String lastSeen, String phoneNumber, String password, String fcmToken, Boolean hideAge, Boolean hideName, Boolean hideProfile, Boolean hideLocation, Boolean typing, double latitude, double longitude, int maxDistance, int ageRange, Boolean isLocationHidden, Boolean isAgeHidden, Boolean isNameHidden, Boolean isProfileHidden, Boolean pp, Boolean p, String premiumEndDate, String premiumStartDate, Boolean hot, Boolean completeprofile, Boolean hotProfieOrNormalProfile, Boolean hotProfile, String hotStartDate, String hotEndDate, Boolean status, Map<String, Boolean> likesList, Map<String, Boolean> visitsList, Map<String, Boolean> blockedList, Map<String, Boolean> likedList, Map<String, Boolean> dislikedList, Map<String, Boolean> matches, List<String> notifications, double walletBalance, Long deletionRequestedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.description = description;
        this.dob = dob;
        this.educationLevel = educationLevel;
        this.gender = gender;
        this.imageurl = imageurl;
        this.imageurl1 = imageurl1;
        this.imageurl2 = imageurl2;
        this.imageurl3 = imageurl3;
        this.imageurl4 = imageurl4;
        this.imageurl5 = imageurl5;
        this.jobType = jobType;
        this.area = area;
        this.state = state;
        this.maritalStatus = maritalStatus;
        this.motherTongue = motherTongue;
        this.personality = personality;
        this.religion = religion;
        this.prefGender = prefGender;
        this.lastSeen = lastSeen;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.fcmToken = fcmToken;
        this.hideAge = hideAge;
        this.hideName = hideName;
        this.hideProfile = hideProfile;
        this.hideLocation = hideLocation;
        this.typing = typing;
        this.latitude = latitude;
        this.longitude = longitude;
        this.maxDistance = maxDistance;
        this.ageRange = ageRange;
        this.isLocationHidden = isLocationHidden;
        this.isAgeHidden = isAgeHidden;
        this.isNameHidden = isNameHidden;
        this.isProfileHidden = isProfileHidden;
        this.pp = pp;
        this.p = p;
        this.premiumEndDate = premiumEndDate;
        this.premiumStartDate = premiumStartDate;
        this.hot = hot;
        this.completeprofile = completeprofile;
        this.hotProfieOrNormalProfile = hotProfieOrNormalProfile;
        this.hotProfile = hotProfile;
        this.hotStartDate = hotStartDate;
        this.hotEndDate = hotEndDate;
        this.status = status;
        this.likesList = likesList;
        this.visitsList = visitsList;
        this.blockedList = blockedList;
        this.likedList = likedList;
        this.dislikedList = dislikedList;
        this.matches = matches;
        this.notifications = notifications;
        this.walletBalance = walletBalance;
        this.deletionRequestedAt = deletionRequestedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getImageurl1() {
        return imageurl1;
    }

    public void setImageurl1(String imageurl1) {
        this.imageurl1 = imageurl1;
    }

    public String getImageurl2() {
        return imageurl2;
    }

    public void setImageurl2(String imageurl2) {
        this.imageurl2 = imageurl2;
    }

    public String getImageurl3() {
        return imageurl3;
    }

    public void setImageurl3(String imageurl3) {
        this.imageurl3 = imageurl3;
    }

    public String getImageurl4() {
        return imageurl4;
    }

    public void setImageurl4(String imageurl4) {
        this.imageurl4 = imageurl4;
    }

    public String getImageurl5() {
        return imageurl5;
    }

    public void setImageurl5(String imageurl5) {
        this.imageurl5 = imageurl5;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getMotherTongue() {
        return motherTongue;
    }

    public void setMotherTongue(String motherTongue) {
        this.motherTongue = motherTongue;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getPrefGender() {
        return prefGender;
    }

    public void setPrefGender(String prefGender) {
        this.prefGender = prefGender;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Boolean getHideAge() {
        return hideAge;
    }

    public void setHideAge(Boolean hideAge) {
        this.hideAge = hideAge;
    }

    public Boolean getHideName() {
        return hideName;
    }

    public void setHideName(Boolean hideName) {
        this.hideName = hideName;
    }

    public Boolean getHideProfile() {
        return hideProfile;
    }

    public void setHideProfile(Boolean hideProfile) {
        this.hideProfile = hideProfile;
    }

    public Boolean getHideLocation() {
        return hideLocation;
    }

    public void setHideLocation(Boolean hideLocation) {
        this.hideLocation = hideLocation;
    }

    public Boolean getTyping() {
        return typing;
    }

    public void setTyping(Boolean typing) {
        this.typing = typing;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public int getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(int ageRange) {
        this.ageRange = ageRange;
    }

    public Boolean getLocationHidden() {
        return isLocationHidden;
    }

    public void setLocationHidden(Boolean locationHidden) {
        isLocationHidden = locationHidden;
    }

    public Boolean getAgeHidden() {
        return isAgeHidden;
    }

    public void setAgeHidden(Boolean ageHidden) {
        isAgeHidden = ageHidden;
    }

    public Boolean getNameHidden() {
        return isNameHidden;
    }

    public void setNameHidden(Boolean nameHidden) {
        isNameHidden = nameHidden;
    }

    public Boolean getProfileHidden() {
        return isProfileHidden;
    }

    public void setProfileHidden(Boolean profileHidden) {
        isProfileHidden = profileHidden;
    }

    public Boolean getPp() {
        return pp;
    }

    public void setPp(Boolean pp) {
        this.pp = pp;
    }

    public Boolean getP() {
        return p;
    }

    public void setP(Boolean p) {
        this.p = p;
    }

    public String getPremiumEndDate() {
        return premiumEndDate;
    }

    public void setPremiumEndDate(String premiumEndDate) {
        this.premiumEndDate = premiumEndDate;
    }

    public String getPremiumStartDate() {
        return premiumStartDate;
    }

    public void setPremiumStartDate(String premiumStartDate) {
        this.premiumStartDate = premiumStartDate;
    }

    public Boolean getHot() {
        return hot;
    }

    public void setHot(Boolean hot) {
        this.hot = hot;
    }

    public Boolean getCompleteprofile() {
        return completeprofile;
    }

    public void setCompleteprofile(Boolean completeprofile) {
        this.completeprofile = completeprofile;
    }

    public Boolean getHotProfieOrNormalProfile() {
        return hotProfieOrNormalProfile;
    }

    public void setHotProfieOrNormalProfile(Boolean hotProfieOrNormalProfile) {
        this.hotProfieOrNormalProfile = hotProfieOrNormalProfile;
    }

    public Boolean getHotProfile() {
        return hotProfile;
    }

    public void setHotProfile(Boolean hotProfile) {
        this.hotProfile = hotProfile;
    }

    public String getHotStartDate() {
        return hotStartDate;
    }

    public void setHotStartDate(String hotStartDate) {
        this.hotStartDate = hotStartDate;
    }

    public String getHotEndDate() {
        return hotEndDate;
    }

    public void setHotEndDate(String hotEndDate) {
        this.hotEndDate = hotEndDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Map<String, Boolean> getLikesList() {
        return likesList;
    }

    public void setLikesList(Map<String, Boolean> likesList) {
        this.likesList = likesList;
    }

    public Map<String, Boolean> getVisitsList() {
        return visitsList;
    }

    public void setVisitsList(Map<String, Boolean> visitsList) {
        this.visitsList = visitsList;
    }

    public Map<String, Boolean> getBlockedList() {
        return blockedList;
    }

    public void setBlockedList(Map<String, Boolean> blockedList) {
        this.blockedList = blockedList;
    }

    public Map<String, Boolean> getLikedList() {
        return likedList;
    }

    public void setLikedList(Map<String, Boolean> likedList) {
        this.likedList = likedList;
    }

    public Map<String, Boolean> getDislikedList() {
        return dislikedList;
    }

    public void setDislikedList(Map<String, Boolean> dislikedList) {
        this.dislikedList = dislikedList;
    }

    public Map<String, Boolean> getMatches() {
        return matches;
    }

    public void setMatches(Map<String, Boolean> matches) {
        this.matches = matches;
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Long getDeletionRequestedAt() {
        return deletionRequestedAt;
    }

    public void setDeletionRequestedAt(Long deletionRequestedAt) {
        this.deletionRequestedAt = deletionRequestedAt;
    }
}