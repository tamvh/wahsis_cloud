/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.database;

/**
 *
 * @author khiemnv
 */
public class PartnerServerCompany {

    private long company_id;
    private long partner_server_id;

    public PartnerServerCompany() {

    }

    public long getPartnerServerId() {
        return partner_server_id;
    }

    public void setPartnerServerId(long _partner_server_id) {
        this.partner_server_id = _partner_server_id;
    }

    public long getCompanyId() {
        return company_id;
    }

    public void setCompanyId(long value) {
        this.company_id = value;
    }
}
