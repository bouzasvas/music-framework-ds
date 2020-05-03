package gr.aueb.ds.music.framework.requests;

import gr.aueb.ds.music.framework.nodes.api.Node;

import java.io.Serializable;

/**
 * This type (interface) is used as Request Object
 * from Consumer to Broker
 */
public interface GetMasterBrokerRequest extends Node, Serializable { }
