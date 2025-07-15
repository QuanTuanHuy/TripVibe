package config

// TopicConfig contains configuration for creating topics.
type TopicConfig struct {
	Name              string             `json:"name" yaml:"name" mapstructure:"name"`
	NumPartitions     int32              `json:"num_partitions" yaml:"num_partitions" mapstructure:"num_partitions"`
	ReplicationFactor int16              `json:"replication_factor" yaml:"replication_factor" mapstructure:"replication_factor"`
	ConfigEntries     map[string]*string `json:"config_entries,omitempty" yaml:"config_entries,omitempty" mapstructure:"config_entries"`
}

// TopicDescription provides detailed information about a Kafka topic.
type TopicDescription struct {
	Name              string                 `json:"name"`
	NumPartitions     int32                  `json:"num_partitions"`
	ReplicationFactor int16                  `json:"replication_factor"`
	Partitions        []PartitionDescription `json:"partitions"`
	ConfigEntries     map[string]*string     `json:"config_entries,omitempty"`
}

// PartitionDescription provides information about a topic partition.
type PartitionDescription struct {
	Partition int32   `json:"partition"`
	Leader    int32   `json:"leader"`
	Replicas  []int32 `json:"replicas"`
	ISR       []int32 `json:"isr"` // In-Sync Replicas
}

// ConsumerGroupDescription provides detailed information about a consumer group.
type ConsumerGroupDescription struct {
	GroupID      string              `json:"group_id"`
	State        string              `json:"state"`
	ProtocolType string              `json:"protocol_type"`
	Protocol     string              `json:"protocol"`
	Members      []MemberDescription `json:"members"`
	Coordinator  int32               `json:"coordinator"`
}

// MemberDescription provides information about a consumer group member.
type MemberDescription struct {
	MemberID         string `json:"member_id"`
	ClientID         string `json:"client_id"`
	ClientHost       string `json:"client_host"`
	MemberMetadata   []byte `json:"member_metadata,omitempty"`
	MemberAssignment []byte `json:"member_assignment,omitempty"`
}

// ClusterMetadata provides information about the Kafka cluster.
type ClusterMetadata struct {
	Brokers []BrokerInfo `json:"brokers"`
	Topics  []string     `json:"topics"`
}

// BrokerInfo provides information about a Kafka broker.
type BrokerInfo struct {
	ID   int32  `json:"id"`
	Host string `json:"host"`
}
