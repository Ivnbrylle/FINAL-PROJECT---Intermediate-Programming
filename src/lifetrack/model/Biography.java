package lifetrack.model;

public class Biography {
    private int userId;
    private String fullName = "";
    private String dateOfBirth = "";
    private String placeOfBirth = "";
    private String gender = "";
    private String nationality = "";
    private String primaryEdu = "";
    private String secondaryEdu = "";
    private String collegeEdu = "";
    private String familyBackground = "";
    private String profilePicture = "";
    private String otherInfo = "";
    private String email = "";
    private String bio = "";
    private String location = "";
    private String website = "";
    private String occupation = "";
    private String socialLinks = "";

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v == null ? "" : v; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String v) { this.dateOfBirth = v == null ? "" : v; }

    public String getPlaceOfBirth() { return placeOfBirth; }
    public void setPlaceOfBirth(String v) { this.placeOfBirth = v == null ? "" : v; }

    public String getGender() { return gender; }
    public void setGender(String v) { this.gender = v == null ? "" : v; }

    public String getNationality() { return nationality; }
    public void setNationality(String v) { this.nationality = v == null ? "" : v; }

    public String getPrimaryEdu() { return primaryEdu; }
    public void setPrimaryEdu(String v) { this.primaryEdu = v == null ? "" : v; }

    public String getSecondaryEdu() { return secondaryEdu; }
    public void setSecondaryEdu(String v) { this.secondaryEdu = v == null ? "" : v; }

    public String getCollegeEdu() { return collegeEdu; }
    public void setCollegeEdu(String v) { this.collegeEdu = v == null ? "" : v; }

    public String getFamilyBackground() { return familyBackground; }
    public void setFamilyBackground(String v) { this.familyBackground = v == null ? "" : v; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String v) { this.profilePicture = v == null ? "" : v; }

    public String getOtherInfo() { return otherInfo; }
    public void setOtherInfo(String v) { this.otherInfo = v == null ? "" : v; }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v == null ? "" : v; }

    public String getBio() { return bio; }
    public void setBio(String v) { this.bio = v == null ? "" : v; }

    public String getLocation() { return location; }
    public void setLocation(String v) { this.location = v == null ? "" : v; }

    public String getWebsite() { return website; }
    public void setWebsite(String v) { this.website = v == null ? "" : v; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String v) { this.occupation = v == null ? "" : v; }

    /**
     * Free-form lines, one per social: {@code platform=url}.
     * Example: {@code linkedin=linkedin.com/in/alex}.
     */
    public String getSocialLinks() { return socialLinks; }
    public void setSocialLinks(String v) { this.socialLinks = v == null ? "" : v; }
}
