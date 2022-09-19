package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class Avatar {
    private long id;
    private String head_shot;
    private String head_src;
    private String torso_src;
    private String left_arm_src;
    private String right_arm_src;
    private String left_leg_src;
    private String right_leg_src;
    private int eyesId;
    private int mouthId;
    private int eyebrowsId;
    private int extrasId;
    private String complexion;

    public Avatar() {
    }

    public Avatar(String head_shot,
                  String head_src,
                  String torso_src,
                  String left_arm_src,
                  String right_arm_src,
                  String left_leg_src,
                  String right_leg_src,
                  int eyesId,
                  int mouthId,
                  int eyebrowsId,
                  int extrasId,
                  String complexion) {
        this.head_shot = head_shot;
        this.head_src = head_src;
        this.torso_src = torso_src;
        this.left_arm_src = left_arm_src;
        this.right_arm_src = right_arm_src;
        this.left_leg_src = left_leg_src;
        this.right_leg_src = right_leg_src;
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
                ", torso_src='" + torso_src + '\'' +
                ", left_arm_src='" + left_arm_src + '\'' +
                ", right_arm_src='" + right_arm_src + '\'' +
                ", left_leg_src='" + left_leg_src + '\'' +
                ", right_leg_src='" + right_leg_src + '\'' +
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

    public String getTorsoSrc() {
        return torso_src;
    }

    public Avatar setTorsoSrc(String torso_src) {
        this.torso_src = torso_src;
        return this;
    }

    public String getLeftArmSrc() {
        return left_arm_src;
    }

    public Avatar setLeftArmSrc(String left_arm_src) {
        this.left_arm_src = left_arm_src;
        return this;
    }

    public String getRightArmSrc() {
        return right_arm_src;
    }

    public Avatar setRightArmSrc(String right_arm_src) {
        this.right_arm_src = right_arm_src;
        return this;
    }

    public String getLeftLegSrc() {
        return left_leg_src;
    }

    public Avatar setLeftLegSrc(String left_leg_src) {
        this.left_leg_src = left_leg_src;
        return this;
    }

    public String getRightLegSrc() {
        return right_leg_src;
    }

    public Avatar setRightLegSrc(String right_leg_src) {
        this.right_leg_src = right_leg_src;
        return this;
    }

    public int getEyesId() {
        return eyesId;
    }

    public Avatar setEyesId(int eyesId) {
        this.eyesId = eyesId;
        return this;
    }

    public int getMouthId() {
        return mouthId;
    }

    public Avatar setMouthId(int mouthId) {
        this.mouthId = mouthId;
        return this;
    }

    public int getEyebrowsId() {
        return eyebrowsId;
    }

    public Avatar setEyebrowsId(int eyebrowsId) {
        this.eyebrowsId = eyebrowsId;
        return this;
    }

    public int getExtrasId() {
        return extrasId;
    }

    public Avatar setExtrasId(int extrasId) {
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
