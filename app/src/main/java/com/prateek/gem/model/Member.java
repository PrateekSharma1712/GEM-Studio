package com.prateek.gem.model;

import java.io.Serializable;
import java.util.Comparator;

import android.support.v4.os.ParcelableCompat;

public class Member extends ParcelableCompat implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4119273519399807458L;
	private int memberId;
	private int memberIdServer;
	private String phoneNumber;
	private String memberName;
	private int groupIdFk;
	private String gcmregNo;
	
	public Member() {
		super();
	}
	
	public Member(int memberId, int memberIdServer, String phoneNumber,
			String memberName, int groupIdFk, String gcmregNo) {
		super();
		this.memberId = memberId;
		this.memberIdServer = memberIdServer;
		this.phoneNumber = phoneNumber;
		this.memberName = memberName;
		this.groupIdFk = groupIdFk;
		this.gcmregNo = gcmregNo;
	}

	@Override
	public String toString() {
		return "Member [memberId=" + memberId + ", memberIdServer="
				+ memberIdServer + ", phoneNumber=" + phoneNumber
				+ ", memberName=" + memberName + ", groupIdFk=" + groupIdFk
				+ ", gcmregNo=" + gcmregNo + "]";
	}

	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public int getGroupIdFk() {
		return groupIdFk;
	}
	public void setGroupIdFk(int groupIdFk) {
		this.groupIdFk = groupIdFk;
	}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        if (!phoneNumber.equals(member.phoneNumber)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return phoneNumber.hashCode();
    }

    public int getMemberIdServer() {
		return memberIdServer;
	}
	public void setMemberIdServer(int memberIdServer) {
		this.memberIdServer = memberIdServer;
	}

	public String getGcmregNo() {
		return gcmregNo;
	}
	public void setGcmregNo(String gcmregNo) {
		this.gcmregNo = gcmregNo;
	}

	public static Comparator<Member> memberComparator = new Comparator<Member>() {
		
		@Override
		public int compare(Member lhs, Member rhs) {
			// TODO Auto-generated method stub
			return lhs.getMemberName().compareTo(rhs.getMemberName());
		}
	};
}
