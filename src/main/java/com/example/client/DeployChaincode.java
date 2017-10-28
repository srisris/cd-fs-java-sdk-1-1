package com.example.client;

import static java.lang.String.format;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.User;

public class DeployChaincode {

  public static void main(String[] args) {

  }

  public void install(HFClient client, Channel channel, Org sampleOrg) throws Exception {
    final String channelName = channel.getName();
    out("Running channel %s", channelName);
    // channel.setTransactionWaitTime(config.getTransactionWaitTime());
    // channel.setDeployWaitTime(config.getDeployWaitTime());

    final ChaincodeID chaincodeID;
    Collection<ProposalResponse> responses;
    Collection<ProposalResponse> successful = new LinkedList<>();
    Collection<ProposalResponse> failed = new LinkedList<>();

    chaincodeID = ChaincodeID.newBuilder().setName("loyal").setVersion("7").build();

    client.setUserContext(sampleOrg.getAdmin());

    out("Creating install proposal");

    InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
    installProposalRequest.setChaincodeID(chaincodeID);

    installProposalRequest.setChaincodeSourceLocation(new File("D:/dev/eclipse/cd-cc-java"));

    installProposalRequest.setChaincodeVersion("1");
    installProposalRequest.setChaincodeLanguage(Type.JAVA);
    installProposalRequest.setChaincodePath(null);

    out("Sending install proposal");

    ////////////////////////////
    // only a client from the same org as the peer can issue an install request
    int numInstallProposal = 0;

    Set<Peer> peersFromOrg = sampleOrg.getPeers();
    numInstallProposal = numInstallProposal + peersFromOrg.size();
    responses = null;
    responses = client.sendInstallProposal(installProposalRequest, peersFromOrg);

    for (ProposalResponse response : responses) {
      if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
        out("Successful install proposal response Txid: %s from peer %s", response.getTransactionID(),
            response.getPeer().getName());
        successful.add(response);
      } else {
        failed.add(response);
      }
    }

    SDKUtils.getProposalConsistencySets(responses);
    // // }
    out("Received %d install proposal responses. Successful+verified: %d . Failed: %d", numInstallProposal,
        successful.size(), failed.size());

    if (failed.size() > 0) {
      ProposalResponse first = failed.iterator().next();
      // fail("Not enough endorsers for install :" + successful.size() + ". " +
      // first.getMessage());
    }

  }

  static void out(String format, Object... args) {

    System.err.flush();
    System.out.flush();

    System.out.println(format(format, args));
    System.err.flush();
    System.out.flush();

  }

  class Org {
    public User getAdmin() {
      return null;
    }

    public Set<Peer> getPeers() {
      return null;
    }

  }

}