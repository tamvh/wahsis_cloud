package com.wahsis.iot.database;

import java.util.List;

/**
 *
 * @author khiemnv
 */
public interface PartnerServerModel {
     public int selectAllPartnerServer(List<PartnerServer> listPartnerServer, List<PartnerServerCompany> listPartnerServerCompany);
}
