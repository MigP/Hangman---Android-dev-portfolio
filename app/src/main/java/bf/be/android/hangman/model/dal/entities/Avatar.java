package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class Avatar {
    private long id;
    private String head_shot;
    private String head_src;
    private int head_left;
    private int head_bottom;
    private String torso_src;
    private int torso_left;
    private int torso_bottom;
    private String left_arm_src;
    private int left_arm_left;
    private int left_arm_bottom;
    private String right_arm_src;
    private int right_arm_left;
    private int right_arm_bottom;
    private String left_leg_src;
    private int left_leg_left;
    private int left_leg_bottom;
    private String right_leg_src;
    private int right_leg_left;
    private int right_leg_bottom;
    private Integer eyesId;
    private Integer mouthId;
    private Integer eyebrowsId;
    private Integer extrasId;
    private String complexion;

    public Avatar() {
    }

    public Avatar(String head_shot,
                  String head_src,
                  int head_left,
                  int head_bottom,
                  String torso_src,
                  int torso_left,
                  int torso_bottom,
                  String left_arm_src,
                  int left_arm_left,
                  int left_arm_bottom,
                  String right_arm_src,
                  int right_arm_left,
                  int right_arm_bottom,
                  String left_leg_src,
                  int left_leg_left,
                  int left_leg_bottom,
                  String right_leg_src,
                  int right_leg_left,
                  int right_leg_bottom,
                  Integer eyesId,
                  Integer mouthId,
                  Integer eyebrowsId,
                  Integer extrasId,
                  String complexion) {
        this.head_shot = head_shot;
        this.head_src = head_src;
        this.head_left = head_left;
        this.head_bottom = head_bottom;

        this.torso_src = torso_src;
        this.torso_left = torso_left;
        this.torso_bottom = torso_bottom;

        this.left_arm_src = left_arm_src;
        this.left_arm_left = left_arm_left;
        this.left_arm_bottom = left_arm_bottom;

        this.right_arm_src = right_arm_src;
        this.right_arm_left = right_arm_left;
        this.right_arm_bottom = right_arm_bottom;

        this.left_leg_src = left_leg_src;
        this.left_leg_left = left_leg_left;
        this.left_leg_bottom = left_leg_bottom;

        this.right_leg_src = right_leg_src;
        this.right_leg_left = right_leg_left;
        this.right_leg_bottom = right_leg_bottom;

        this.eyesId = eyesId;
        this.mouthId = mouthId;
        this.eyebrowsId = eyebrowsId;
        this.extrasId = extrasId;
        this.complexion = complexion;
    }

    @NonNull
    @Override
    public String toString() {
        return "Avatar{" +
                "id='" + id + '\'' +
                ", head_shot='" + head_shot + '\'' +
                ", head_src='" + head_src + '\'' +
                ", head_left='" + head_left + '\'' +
                ", head_bottom='" + head_bottom + '\'' +
                ", torso_src='" + torso_src + '\'' +
                ", torso_left='" + torso_left + '\'' +
                ", torso_bottom='" + torso_bottom + '\'' +
                ", left_arm_src='" + left_arm_src + '\'' +
                ", left_arm_left='" + left_arm_left + '\'' +
                ", left_arm_bottom='" + torso_bottom + '\'' +
                ", right_arm_src='" + right_arm_src + '\'' +
                ", right_arm_left='" + right_arm_left + '\'' +
                ", right_arm_bottom='" + right_arm_bottom + '\'' +
                ", left_leg_src='" + left_leg_src + '\'' +
                ", left_leg_left='" + left_leg_left + '\'' +
                ", left_leg_bottom='" + left_leg_bottom + '\'' +
                ", right_leg_src='" + right_leg_src + '\'' +
                ", right_leg_left='" + right_leg_left + '\'' +
                ", right_leg_bottom='" + right_leg_bottom + '\'' +
                ", eyesId='" + eyesId + '\'' +
                ", mouthId='" + mouthId + '\'' +
                ", eyebrowsId='" + eyebrowsId + '\'' +
                ", extrasId='" + extrasId + '\'' +
                ", complexion='" + complexion + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public Avatar setId(long id) {
        this.id = id;
        return this;
    }

    public String getHeadShot() {
        return head_shot;
    }

    public Avatar setHeadShot(String head_shot) {
        this.head_shot = head_shot;
        return this;
    }

    public String getHeadSrc() {
        return head_src;
    }

    public Avatar setHeadSrc(String head_src) {
        this.head_src = head_src;
        return this;
    }

    public int getHeadLeft() {
        return head_left;
    }

    public Avatar setHeadLeft(int head_left) {
        this.head_left = head_left;
        return this;
    }

    public int getHeadBottom() {
        return head_bottom;
    }

    public Avatar setHeadBottom(int head_bottom) {
        this.head_bottom = head_bottom;
        return this;
    }

    public String getTorsoSrc() {
        return torso_src;
    }

    public Avatar setTorsoSrc(String torso_src) {
        this.torso_src = torso_src;
        return this;
    }

    public int getTorsoLeft() {
        return torso_left;
    }

    public Avatar setTorsoLeft(int torso_left) {
        this.torso_left = torso_left;
        return this;
    }

    public int getTorsoBottom() {
        return torso_bottom;
    }

    public Avatar setTorsoBottom(int torso_bottom) {
        this.torso_bottom = torso_bottom;
        return this;
    }

    public String getLeftArmSrc() {
        return left_arm_src;
    }

    public Avatar setLeftArmSrc(String left_arm_src) {
        this.left_arm_src = left_arm_src;
        return this;
    }

    public int getLeftArmLeft() {
        return left_arm_left;
    }

    public Avatar setLeftArmLeft(int left_arm_left) {
        this.left_arm_left = left_arm_left;
        return this;
    }

    public int getLeftArmBottom() {
        return left_arm_bottom;
    }

    public Avatar setLeftArmBottom(int left_arm_bottom) {
        this.left_arm_bottom = left_arm_bottom;
        return this;
    }

    public String getRightArmSrc() {
        return right_arm_src;
    }

    public Avatar setRightArmSrc(String right_arm_src) {
        this.right_arm_src = right_arm_src;
        return this;
    }

    public int getRightArmLeft() {
        return right_arm_left;
    }

    public Avatar setRightArmLeft(int right_arm_left) {
        this.right_arm_left = right_arm_left;
        return this;
    }

    public int getRightArmBottom() {
        return right_arm_bottom;
    }

    public Avatar setRightArmBottom(int right_arm_bottom) {
        this.right_arm_bottom = right_arm_bottom;
        return this;
    }

    public String getLeftLegSrc() {
        return left_leg_src;
    }

    public Avatar setLeftLegSrc(String left_leg_src) {
        this.left_leg_src = left_leg_src;
        return this;
    }

    public int getLeftLegLeft() {
        return left_leg_left;
    }

    public Avatar setLeftLegLeft(int left_leg_left) {
        this.left_leg_left = left_leg_left;
        return this;
    }

    public int getLeftLegBottom() {
        return left_leg_bottom;
    }

    public Avatar setLeftLegBottom(int left_leg_bottom) {
        this.left_leg_bottom = left_leg_bottom;
        return this;
    }

    public String getRightLegSrc() {
        return right_leg_src;
    }

    public Avatar setRightLegSrc(String right_leg_src) {
        this.right_leg_src = right_leg_src;
        return this;
    }

    public int getRightLegLeft() {
        return right_leg_left;
    }

    public Avatar setRightLegLeft(int right_leg_left) {
        this.right_leg_left = right_leg_left;
        return this;
    }

    public int getRightLegBottom() {
        return right_leg_bottom;
    }

    public Avatar setRightLegBottom(int right_leg_bottom) {
        this.right_leg_bottom = right_leg_bottom;
        return this;
    }

    public Integer getEyesId() {
        return eyesId;
    }

    public Avatar setEyesId(Integer eyesId) {
        this.eyesId = eyesId;
        return this;
    }

    public Integer getMouthId() {
        return mouthId;
    }

    public Avatar setMouthId(Integer mouthId) {
        this.mouthId = mouthId;
        return this;
    }

    public Integer getEyebrowsId() {
        return eyebrowsId;
    }

    public Avatar setEyebrowsId(Integer eyebrowsId) {
        this.eyebrowsId = eyebrowsId;
        return this;
    }

    public Integer getExtrasId() {
        return extrasId;
    }

    public Avatar setExtrasId(Integer extrasId) {
        this.extrasId = extrasId;
        return this;
    }

    public String getComplexion() {
        return complexion;
    }

    public Avatar setComplexion(String complexion) {
        this.complexion = complexion;
        return this;
    }
}
