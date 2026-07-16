# Packet catcher

A simple Minecraft mod for capturing packets and exporting them to a file.

## Configuration

### Formatting

Supported title formats:
1. `%time%` - Formatted time
2. `%type%` - Packet flow type (example: `serverbound`)
3. `%id%` - Packet id (example: `minecraft:intention`)
4. `%values%` - Formatted values
5. `%nl%` - New line

Supported value formats:
1. `%name%` - Name of value (example: `protocolVersion`)
2. `%value%` - Value (example: `776`)

Time formatting is simply Java's [DateTimeFormatter pattern](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns)

### Filtering

Each packet contains namespace (example: clientbound `minecraft:pong_response`)