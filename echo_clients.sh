#!/bin/bash

# Check for the required number of arguments
if [ "$#" -ne 3 ]; then
    echo "Usage: $0 <server_port> <number_of_clients> <number_of_messages>"
    exit 1
fi

SERVER_PORT=$1
NUM_CLIENTS=$2
NUM_MESSAGES=$3

# Generate a descriptive message
generate_message() {
    local client_num=$1
    local message_num=$2
    echo "Client $client_num: Message $message_num"
}

# Function to simulate a client
simulate_client() {
    local client_num=$1
    for ((i=0; i<$NUM_MESSAGES; i++)); do
        generate_message $client_num $i
    done | nc -w 1 localhost $SERVER_PORT
}

# Spawn the clients
for ((i=0; i<$NUM_CLIENTS; i++)); do
    simulate_client $i &
done

# Wait for all background jobs to finish
wait
