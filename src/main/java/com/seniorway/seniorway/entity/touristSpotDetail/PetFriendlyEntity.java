package com.seniorway.seniorway.entity.touristSpotDetail;

import jakarta.persistence.*;
import com.seniorway.seniorway.entity.common.BaseTimeEntity;

@Entity
@Table(name = "pet_friendly_info", uniqueConstraints = @UniqueConstraint(name = "uk_content", columnNames = "content_id"))
public class PetFriendlyEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_id", nullable = false, length = 50)
    private String contentId;

    @Column(name = "acmpy_need_mtr", length = 500)
    private String acmpyNeedMtr;

    @Column(name = "rela_acdnt_risk_mtr", length = 500)
    private String relaAcdntRiskMtr;

    @Column(name = "acmpy_type_cd", length = 50)
    private String acmpyTypeCd;

    @Column(name = "rela_poses_fclty", length = 500)
    private String relaPosesFclty;

    @Column(name = "rela_frnsh_prdlst", length = 500)
    private String relaFrnshPrdlst;

    @Column(name = "etc_acmpy_info", length = 500)
    private String etcAcmpyInfo;

    @Column(name = "rela_purc_prdlst", length = 500)
    private String relaPurcPrdlst;

    @Column(name = "acmpy_psbl_cpam", length = 200)
    private String acmpyPsblCpam;

    @Column(name = "rela_rntl_prdlst", length = 500)
    private String relaRntlPrdlst;

    public Long getId() {
        return id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public void setAcmpyNeedMtr(String acmpyNeedMtr) {
        this.acmpyNeedMtr = acmpyNeedMtr;
    }

    public void setRelaAcdntRiskMtr(String relaAcdntRiskMtr) {
        this.relaAcdntRiskMtr = relaAcdntRiskMtr;
    }

    public void setAcmpyTypeCd(String acmpyTypeCd) {
        this.acmpyTypeCd = acmpyTypeCd;
    }

    public void setRelaPosesFclty(String relaPosesFclty) {
        this.relaPosesFclty = relaPosesFclty;
    }

    public void setRelaFrnshPrdlst(String relaFrnshPrdlst) {
        this.relaFrnshPrdlst = relaFrnshPrdlst;
    }

    public void setEtcAcmpyInfo(String etcAcmpyInfo) {
        this.etcAcmpyInfo = etcAcmpyInfo;
    }

    public void setRelaPurcPrdlst(String relaPurcPrdlst) {
        this.relaPurcPrdlst = relaPurcPrdlst;
    }

    public void setAcmpyPsblCpam(String acmpyPsblCpam) {
        this.acmpyPsblCpam = acmpyPsblCpam;
    }

    public void setRelaRntlPrdlst(String relaRntlPrdlst) {
        this.relaRntlPrdlst = relaRntlPrdlst;
    }
}
