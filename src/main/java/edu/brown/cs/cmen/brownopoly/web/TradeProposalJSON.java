package edu.brown.cs.cmen.brownopoly.web;

import edu.brown.cs.cmen.brownopoly.game.TradeProposal;

/**
 * Created by codyyu on 4/27/15.
 */
public class TradeProposalJSON {
    private PlayerJSON initializer;
    private PlayerJSON recipient;
    private String[] initProps;
    private int initMoney;
    private String[] recipProps;
    private int recipMoney;
    private boolean hasTrade;
    public TradeProposalJSON(TradeProposal proposal) {
        if (proposal != null) {
            this.hasTrade = true;
            this.initializer = new PlayerJSON(proposal.getInitializer());
            this.recipient = new PlayerJSON(proposal.getRecipient());
            this.initProps = proposal.getInitProps();
            this.initMoney = proposal.getInitMoney();
            this.recipProps = proposal.getRecipProps();
            this.recipMoney = proposal.getRecipMoney();
        } else {
            this.hasTrade = false;
        }
    }

    public PlayerJSON getInitializer() {
        return initializer;
    }
    public PlayerJSON getRecipient() {
        return recipient;
    }
    public String[] getInitProps() {
        return initProps;
    }
    public int getInitMoney() {
        return initMoney;
    }
    public String[] getRecipProps() {
        return recipProps;
    }
    public int getRecipMoney() {
        return recipMoney;
    }
}
